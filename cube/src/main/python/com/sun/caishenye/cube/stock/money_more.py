import pandas as pd

from com.sun.caishenye.cube.common import utils
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.mm.file_name')
headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K']
year_target = config.get('file.name.stock.mm.target_year')
out_file_name = config.get('file.name.stock.mm.out_file_name')


def run():
    utils.remove_file(file_path, out_file_name)

    __y_current__()
    __y5_top5__()
    __y2_top6__()


def __y_current__():
    df = __read_file__()

    """
    过滤数据。
    """
    header_h = ['预案']
    header_g = config.get('file.name.stock.mm.header.g')
    # df_filtered = df[df['E'].str.contains(utils.year()) & df['H'].isin(header_h) & df['G'] > 5]
    df_filtered1 = df[df['E'].str.contains(r'{}'.format(utils.year())) & df['H'].isin(header_h)]
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df_filtered2 = df_filtered1[df_filtered1['G'] >= header_g]

    """
    写excel。
    """
    df_sort = df_filtered2.sort_values(by='G', ascending=False)
    log.log(df_sort)
    utils.write_excel(file_path, out_file_name, 'mm', df_sort)


def __y5_top5__():
    df = __read_file__()


def __y2_top6__():
    df = __read_file__()

    """
    过滤数据。(近三年)
    """
    header_h = ['实施']
    header_g = 6.0
    # TODO
    year_current = r'{}'.format(year_target + 1)
    year_current_1 = r'{}'.format(year_target)
    year_current_2 = r'{}'.format(year_target - 1)
    years = [year_current, year_current_1, year_current_2]
    df_filtered1 = df[df['E'].str.contains('|'.join(years)) & df['H'].isin(header_h)]  # dataframe
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df2 = df_filtered1[df_filtered1['G'] >= header_g]  # series
    utils.write_excel(file_path, out_file_name, '1_Y2TOP6_3Y', df2.sort_values(by='A'))

    """
    计算
    """
    """
    1. 评分。
        1. 评分为3的，第一名
        2. 评分为2的，且K列包含连续近两年的是第二名
        3. M列，G列平均值排序
    """
    df2['L'] = ''
    df2['M'] = df2['B'].map(df2['B'].value_counts())
    utils.write_excel(file_path, out_file_name, '2_Y2TOP6_COUNT', df2.sort_values(by='A'))

    df_score_1 = df2[df2['M'].eq(3)]

    year_current = r'{}'.format(year_target)
    year_current_1 = r'{}'.format(year_target - 1)
    years = [year_current, year_current_1]
    df_score_2 = df2[df2['M'].eq(2) & df2['K'].str.contains('|'.join(years))]
    df_score_2['M'] = df_score_2['B'].map(df_score_2['B'].value_counts())
    df_score_2 = df_score_2[df_score_2['M'].eq(2)]

    # 以B列分组计算G列的均值(mean)
    df_score_1['N'] = df_score_1.groupby('B')['G'].transform('mean')
    df_score_2['N'] = df_score_2.groupby('B')['G'].transform('mean')
    utils.write_excel(file_path, out_file_name, '3_Y2TOP6_MEAN', pd.concat([df_score_1, df_score_2]).sort_values(by='M'))

    # 为了不影响列的顺序，以A列分组。不写reset_index B列缺失。
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by='N', ascending=False)
    df_score_2 = df_score_2.sort_values(by='N', ascending=False)

    df_score = pd.concat([df_score_1, df_score_2])
    log.log(df_score)
    utils.write_excel(file_path, out_file_name, 'Y2TOP6', df_score)


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)

