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


def run():
    __stock__()


def __stock__():
    if target_date is None:
        end_date = utils.today()
    else:
        end_date = target_date

    _is_work_day = False
    while ~_is_work_day:
        _is_work_day = utils.is_work_day(end_date)
        if _is_work_day:
            break
        else:
            end_date = end_date - timedelta(days=1)
    log.log(r'end date :: {}'.format(end_date))

    utils.remove_file(file_path, r'{}_{}'.format(out_file_name, end_date))
    start_date = end_date - timedelta(days=header_c)

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
    log.log('cal count start')
    df = __cal_d_count__(df)
    log.log('cal count end')
    utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'COUNT', df)

    """
    过滤数据。
    """
    df['C'] = df['C'].astype(str)
    # 只保留最新日期
    df = df[(df['C'].eq(str(end_date))) & ~(df['N'].eq(0))]
    # # drop_duplicates 函数默认保留首次出现的值，如果想保留最后一次出现的值，可以使用keep='last'这样，在去除重复值的过程中，会保留最后一次出现的重复值。
    # df = df.drop_duplicates('B', keep='last')
    # df = df[~(df['N'].eq(0))]

    """
    Sort
    """
    df_result = df.sort_values(by='V', ascending=False)

    # """
    # 过滤数据。
    # """
    # df = df[(df['C'] >= str(start_date)) & (df['C'] <= str(end_date))
    #         & (df['D'] >= header_d)
    #         & (df['O'] >= header_o)]
    #
    # """
    # 计算
    # """
    # df['R'] = ''
    # df['S'] = df['B'].map(df['B'].value_counts())
    # df = df.sort_values(by=['S', 'B'], ascending=[False, True])
    # utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), 'COUNT', df)
    # # log.log(r'{} :: {}'.format(r'{}_{}_{}'.format(out_file_name, target_day, 'COUNT'), df))
    #
    # # drop_duplicates 函数默认保留首次出现的值，如果想保留最后一次出现的值，可以使用keep='last'这样，在去除重复值的过程中，会保留最后一次出现的重复值。
    # df_result = df.drop_duplicates('B', keep='last')
    #
    # # 只保留最新日期
    # df_result = df_result[df_result['C'].eq(str(end_date))]
    # df_result = df_result.sort_values(by=['S', 'B'], ascending=[False, True])
    #
    """
    写excel。
    """
    utils.write_excel(file_path, r'{}_{}'.format(out_file_name, end_date), out_file_name, df_result)
    log.log(r'{} :: {}'.format(r'{}_{}'.format(out_file_name, end_date), df_result))


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


def __cal_d_count__(df):
    # 确保日期列为 datetime 类型，用于日期计算
    df['C'] = pd.to_datetime(df['C'])
    grouped = df.groupby(['A', 'B'])

    def __count_consecutive_days__(group):
        # 用于计算连续日期的天数，需要排序
        group = group.sort_values('C')
        group['is_valid'] = group['D'] >= header_d
        # 检查 C 列是否连续。
        group['consecutive'] = group['C'].diff().dt.days.eq(1) & group['is_valid']
        # 计算连续日期的天数。
        group['group'] = (~group['consecutive']).cumsum()
        consecutive_days = group.groupby(['group', 'is_valid'])['consecutive'].cumsum()

        group['U'] = ''
        group['V'] = consecutive_days.where(group['is_valid'], 0)
        return group

    result = grouped.apply(__count_consecutive_days__).reset_index(drop=True)
    return result


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)
