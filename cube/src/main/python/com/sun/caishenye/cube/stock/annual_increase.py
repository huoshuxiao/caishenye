"""
年度涨跌幅
"""
import os

import pandas as pd

from com.sun.caishenye.cube.common import consts, utils
from com.sun.caishenye.cube.config import config, log

file_path = os.path.join(config.get('file.path'), consts.STOCK)
execute_flg = config.get('file.name.stock.ai.run')
out_file_name = config.get('file.name.stock.ai.file_name.out')
file_names = config.get('file.name.stock.ai.file_name.in')

increases = config.get('file.name.stock.ai.header.e')
headers = config.get('file.name.stock.ai.headers')


def run():
    if execute_flg is None:
        return

    utils.remove_file(file_path, out_file_name)

    df = __load_data__()

    # 扩展列
    __cal_increases__(df)
    df_result = df.sort_values(by=['A','D'], ascending=True)
    utils.write_excel(file_path, out_file_name, 'ai', df_result)
    log.log(r'{}-{} :: {}'.format(out_file_name, 'ai', df_result))

    # 当年
    df_current_year = __y_current__(df_result)
    for i in range(len(headers)):
        _df_result = df_current_year[df_current_year['E'] > increases[i]]
        utils.write_excel(file_path, out_file_name, r'{}_{}'.format(utils.year(), increases[i]),
                          _df_result.sort_values(by=['E'], ascending=[False]))
        log.log(r'{}-{} :: {}'.format(out_file_name, r'{}_{}'.format(utils.year(), increases[i]), _df_result))

    if config.get('file.name.stock.ai.cal_history_year') is True:
        # 往年
        df_history_year = __not_y_current__(df_result)
        for i in range(len(headers)):
            _df_result = df_history_year[df_history_year['E'] > increases[i]]
            utils.write_excel(file_path, out_file_name, r'{}_{}'.format('Y', increases[i]), _df_result)
            log.log(r'{}-{} :: {}'.format(out_file_name, r'{}_{}'.format('Y', increases[i]), _df_result))

            # COUNT
            df_count = __cal_count__(_df_result)
            utils.write_excel(file_path, out_file_name, r'{}_{}_COUNT'.format('Y', increases[i]), df_count)
            log.log(r'{}-{} :: {}'.format(out_file_name, r'{}_{}_COUNT'.format('Y', increases[i]), df_count))

            # RECOUNT
            df_recount = __cal_recount__(_df_result)
            utils.write_excel(file_path, out_file_name, r'{}_{}_RECOUNT'.format('Y', increases[i]), df_recount)
            log.log(r'{}-{} :: {}'.format(out_file_name, r'{}_{}_RECOUNT'.format('Y', increases[i]), df_recount))


def __cal_count__(df):
    df[config.get('file.name.stock.ai.header.COUNT')] = df.groupby('B')['B'].transform('count')
    df = df.sort_values(by=['A','D', config.get('file.name.stock.ai.header.COUNT')], ascending=[True, True, False])
    return df


def __cal_recount__(df):
    # select D group by B
    df[config.get('file.name.stock.ai.header.RECOUNT')] = df.groupby('B')['D'].transform(__cal_longest_consecutive_years__)
    df = df.sort_values(by=['A','D', config.get('file.name.stock.ai.header.COUNT'), config.get('file.name.stock.ai.header.RECOUNT')],
                        ascending=[True, True, False, False])
    return df


# 计算最长连续年份
def __cal_longest_consecutive_years__(years):
    years = sorted(set(years))
    max_streak = 1
    current_streak = 1
    best_start = years[0]

    start = years[0]
    for i in range(1, len(years)):
        if years[i] == years[i - 1] + 1:
            current_streak += 1
        else:
            if current_streak > max_streak:
                max_streak = current_streak
                best_start = start
            current_streak = 1
            start = years[i]

    # 检查最后一段
    if current_streak > max_streak:
        max_streak = current_streak
        best_start = start

    return f"最长连续 {max_streak} 年：{best_start} - {best_start + max_streak - 1}"

def __y_current__(df):
    year = utils.year()
    df_filtered1 = df[df['D'].eq(year)]
    return df_filtered1


def __not_y_current__(df):
    year = utils.year()
    df_filtered1 = df[~df['D'].eq(year)]
    return df_filtered1


# 扩展列
def __cal_increases__(df) -> None:
    # df['E'] = df[df['E'].eq('-') or df['E'].eq('X')]
    # ETL ('-' or 'X' to NaN)
    df['E'] = pd.to_numeric(df['E'], errors='coerce')

    # df[headers[0]] = df['E'] > increases[0]
    # df[headers[1]] = df['E'] > increases[1]
    # df[headers[2]] = df['E'] > increases[2]
    # df[headers[3]] = df['E'] > increases[3]
    # df[headers[4]] = df['E'] > increases[4]
    # df[headers[5]] = df['E'] > increases[5]
    # df[headers[6]] = df['E'] > increases[6]
    for i in range(len(headers)):
        df[headers[i]] = df['E'] > increases[i]


def __load_data__():
    df_base = utils.read_csv(file_path, file_names[0], ['A', 'B', 'C'])
    df_ai = utils.read_csv(file_path, file_names[1], ['A', 'B', 'C', 'D']).rename(columns={'A':'A','B':'B','C':'D','D':'E'})
    df = pd.merge(df_base[['A', 'C']], df_ai[['A', 'B', 'D', 'E']], on='A', how='right')
    # 按列名字典序排序
    df_sorted = df[sorted(df.columns)]

    return df_sorted