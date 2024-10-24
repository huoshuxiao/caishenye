"""
资金流
  1. 个股
"""


from datetime import timedelta

import pandas as pd

from com.sun.caishenye.cube.common import utils, consts
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.money_flow.stock.file_name.in')
out_file_name = config.get('file.name.stock.money_flow.stock.file_name.out')

headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q']

header_c = config.get('file.name.stock.money_flow.stock.header.c')
header_d = config.get('file.name.stock.money_flow.stock.header.d')
header_o = config.get('file.name.stock.money_flow.stock.header.o')
target_date = config.get('file.name.stock.money_flow.stock.target_date')

execute_flg = config.get('file.name.stock.money_flow.run')


def run():
    if execute_flg is None:
        return

    # 个股
    __stock__()


# 个股
def __stock__():
    """
    确定日期范围
    """
    start_date, end_date = __cal_date_range__()
    log.log(r'start date :: {} end date :: {}'.format(start_date, end_date))

    utils.remove_file(file_path, r'{}_{}'.format(out_file_name, end_date))

    df = __read_file__()
    df['D'] = df['D'].astype(int)
    df['O'] = df['O'].astype(float)

    """
    一次过滤数据，减少数据量。
    """
    df = df[(df['C'] >= str(start_date)) & (df['C'] <= str(end_date))]

    """
    补全数据
    """
    log.log('fill start')
    df = __fill__(df, start_date, end_date)
    log.log('fill end')
    # utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'FILL', df) # 耗时

    """
    计算
    """
    log.log('cal count d start')
    df_d = __cal_v_by_d__(df)
    log.log('cal count d end')
    utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'COUNT_D', df_d)
    log.log(r'{} :: {}'.format('COUNT_D', df_d))

    # log.log('cal count o start')
    # df_o = __cal_o_count__(df)
    # log.log('cal count o end')
    # utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'COUNT_O', df_o)
    # log.log(r'{} :: {}'.format('COUNT_O', df_o))

    """
    过滤数据（二次计算）
    """
    log.log('cal count v start')
    df_d_max = __cal2_v_by_d__(df_d)
    # df_d['C'] = df_d['C'].astype(str)
    # # 只保留最新日期
    # df_d_result = df_d[(df_d['C'].eq(str(end_date))) & ~(df_d['N'].eq(0))]
    # # # drop_duplicates 函数默认保留首次出现的值，如果想保留最后一次出现的值，可以使用keep='last'这样，在去除重复值的过程中，会保留最后一次出现的重复值。
    # # df = df.drop_duplicates('B', keep='last')
    # # df = df[~(df['N'].eq(0))]

    # df_o_max = __cal2_o_count__(df_o)
    # df_o['C'] = df_o['C'].astype(str)
    # # 只保留最新日期
    # df_o_result = df_o[(df_o['C'].eq(str(end_date))) & ~(df_o['N'].eq(0))]
    # # # drop_duplicates 函数默认保留首次出现的值，如果想保留最后一次出现的值，可以使用keep='last'这样，在去除重复值的过程中，会保留最后一次出现的重复值。
    # # df = df.drop_duplicates('B', keep='last')
    # # df = df[~(df['N'].eq(0))]
    log.log('cal count v end')

    """
    Sort
    """
    df_d_result = df_d_max.sort_values(by='V', ascending=False)
    # df_o_result = df_o_max.sort_values(by='V', ascending=False)

    """
    写excel
    """
    utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'MAX_D', df_d_result)
    log.log(r'{} :: {}'.format(r'{}_{}_{}'.format(out_file_name, end_date, 'MAX_D'), df_d_result))

    # utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'MAX_O', df_o_result)
    # log.log(r'{} :: {}'.format(r'{}_{}_{}'.format(out_file_name, end_date, 'MAX_O'), df_o_result))


def __fill__(df, start_date, end_date):
    # 创建一个包含所有日期的完整日期范围的 DataFrame
    date_range = pd.date_range(start=start_date, end=end_date, freq='D').strftime(consts.FORMAT_DATE)

    # 提取所有股票名称
    # stocks = df[['A', 'B']].drop_duplicates()

    # # 使用笛卡尔积生成所有股票和日期的组合
    # cartesian = pd.MultiIndex.from_product([stocks['A'], stocks['B'], date_range], names=['A', 'B', 'C'])
    # cartesian_df = pd.DataFrame(index=cartesian)#.reset_index()
    #
    # # 合并现有数据和完整日期范围，填充缺失值
    # merged_df = pd.merge(cartesian_df, df, on=['A', 'B', 'C'], how='left').fillna({'D': header_d, 'O': header_o})

    # 重新索引函数（按给定的列顺序）
    def __reindex_data__(group):
        indexed_group = group.set_index('C').reindex(date_range).rename_axis('C').reset_index()
        indexed_group['A'] = group['A'].iloc[0]
        indexed_group['B'] = group['B'].iloc[0]
        indexed_group['D'] = indexed_group['D'].fillna(header_d)
        indexed_group['O'] = indexed_group['O'].fillna(header_o)
        indexed_group['N'] = indexed_group['N'].fillna(0)
        return indexed_group[headers]

    # 分组并重新索引
    result = df.groupby(['A', 'B']).apply(__reindex_data__).reset_index(drop=True)
    return result


def __cal_v_by_d__(df):
    # 确保日期列为 datetime 类型，用于日期计算
    df['C'] = pd.to_datetime(df['C'])
    grouped = df.groupby(['A', 'B'])

    def __count_consecutive_days__(group):
        # 用于计算连续日期的天数，需要排序
        group = group.sort_values('C')
        group['is_valid_d'] = group['D'] >= header_d
        # 检查 C 列是否连续。
        group['consecutive_d'] = group['C'].diff().dt.days.eq(1) & group['is_valid_d']
        # 计算连续日期的天数。
        group['group_d'] = (~group['consecutive_d']).cumsum()
        consecutive_days = group.groupby(['group_d', 'is_valid_d'])['consecutive_d'].cumsum()

        group['U'] = ''
        group['V'] = consecutive_days.where(group['is_valid_d'], 0)

        # # 显示小数点后2位
        # group['W'] = r'{:.0%}'.format(sum(group['is_valid_d']) / len(group['D']))
        group['U'] = group['D'] > header_d
        # 正数
        group['W'] = r'{}'.format(sum(group['U']))

        return group

    result = grouped.apply(__count_consecutive_days__).reset_index(drop=True)
    return result


def __cal_v_by_o__(df):
    # 确保日期列为 datetime 类型，用于日期计算
    df['C'] = pd.to_datetime(df['C'])
    grouped = df.groupby(['A', 'B'])

    def __count_consecutive_days__(group):
        # 用于计算连续日期的天数，需要排序
        group = group.sort_values('C')
        group['is_valid_o'] = group['O'] >= header_o
        # 检查 C 列是否连续。
        group['consecutive_o'] = group['C'].diff().dt.days.eq(1) & group['is_valid_o']
        # 计算连续日期的天数。
        group['group_o'] = (~group['consecutive_o']).cumsum()
        consecutive_days = group.groupby(['group_o', 'is_valid_o'])['consecutive_o'].cumsum()

        group['U'] = ''
        group['V'] = consecutive_days.where(group['is_valid_o'], 0)

        # # 显示小数点后2位
        # group['W'] = r'{:.0%}'.format(sum(group['is_valid_d']) / len(group['D']))
        group['U'] = group['O'] > header_d
        # 正数
        group['W'] = r'{}'.format(sum(group['U']))
        return group

    result = grouped.apply(__count_consecutive_days__).reset_index(drop=True)
    return result


def __cal2_v_by_d__(df):
    # 确保日期列为 datetime 类型，用于日期计算
    df['C'] = pd.to_datetime(df['C'])
    grouped = df.groupby(['A', 'B'])

    def __count_v_days__(group):
        max_row = group.loc[group['C'].idxmax()]
        is_max_row = max_row['V'] > 1
        if is_max_row:
            desc_group = group.sort_values(by='C', ascending=False)
            header_d_count = 0
            for _, r in desc_group.iterrows():
                if r['D'] == header_d:
                    header_d_count = header_d_count + 1
                # exit
                elif r['D'] < 0:
                    break
            max_row['V'] = max_row['V'] - header_d_count
        return max_row

    max_result = grouped.apply(__count_v_days__).reset_index(drop=True)
    max_result['C'] = max_result['C'].astype(str)
    return max_result


def __cal2_v_by_o__(df):
    # 确保日期列为 datetime 类型，用于日期计算
    df['C'] = pd.to_datetime(df['C'])
    grouped = df.groupby(['A', 'B'])

    def __count_v_days__(group):
        max_row = group.loc[group['C'].idxmax()]
        is_max_row = max_row['V'] > 1
        if is_max_row:
            desc_group = group.sort_values(by='C', ascending=False)
            header_d_count = 0
            for _, r in desc_group.iterrows():
                if r['D'] == header_d:
                    header_d_count = header_d_count + 1
                # exit
                elif r['O'] < 0:
                    break
            max_row['V'] = max_row['V'] - header_d_count
        return max_row

    max_result = grouped.apply(__count_v_days__).reset_index(drop=True)
    max_result['C'] = max_result['C'].astype(str)
    return max_result


def __cal_date_range__():
    if target_date is None:
        end_date = utils.today()
        if end_date.weekday() == 5:
            end_date = end_date - timedelta(days=1)
        elif end_date.weekday() == 6:
            end_date = end_date - timedelta(days=2)
    else:
        end_date = target_date

    _is_work_day = False
    while ~_is_work_day:
        _is_work_day = utils.is_work_day(end_date)
        if _is_work_day:
            break
        else:
            end_date = end_date - timedelta(days=1)

    start_date = end_date - timedelta(days=header_c)

    return start_date, end_date


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)
