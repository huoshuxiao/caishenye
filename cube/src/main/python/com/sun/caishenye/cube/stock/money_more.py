import pandas as pd

from com.sun.caishenye.cube.common import utils
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.mm.file_name.in')
out_file_name = config.get('file.name.stock.mm.file_name.out')

headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K']
target_year = config.get('file.name.stock.mm.target_year')

execute_flg = config.get('file.name.stock.mm.run')


def run():
    if execute_flg is None:
        return

    utils.remove_file(file_path, out_file_name)

    __y_current__()
    __y2_top6__()
    __y5_top5__()
    __y10_top3__()


def __y_current__():
    df = __read_file__()

    """
    过滤数据。
    """
    header_g = config.get('file.name.stock.mm.header.g')
    header_h = ['预案']
    df_filtered1 = df[df['E'].str.contains(r'{}'.format(utils.year())) & df['H'].isin(header_h)]
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df_filtered2 = df_filtered1[df_filtered1['G'] >= header_g]

    """
    写excel。
    """
    df_result = df_filtered2.sort_values(by='G', ascending=False)
    utils.write_excel(file_path, out_file_name, 'mm', df_result)
    log.log(r'MM :: {}'.format(df_result))


def __y5_top5__():
    df = __read_file__()

    """
    过滤数据。(近六年)
    """
    header_g = config.get('file.name.stock.mm.header.g_y5')
    header_h = ['实施']
    # TODO
    year_current = r'{}'.format(target_year + 1)
    year_current_1 = r'{}'.format(target_year)
    year_current_2 = r'{}'.format(target_year - 1)
    year_current_3 = r'{}'.format(target_year - 2)
    year_current_4 = r'{}'.format(target_year - 3)
    year_current_5 = r'{}'.format(target_year - 4)
    years = [year_current, year_current_1, year_current_2, year_current_3, year_current_4, year_current_5]
    df_filtered1 = df[df['E'].str.contains('|'.join(years)) & df['H'].isin(header_h)]  # dataframe
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df2 = df_filtered1[df_filtered1['G'] >= header_g]  # series
    # utils.write_excel(file_path, out_file_name, '1_Y5TOP5_6Y', df2.sort_values(by='A'))

    """
    计算
    """

    """
    1. 评分。
    2. 均值(mean)。
    """
    # 1. 评分
    df2['L'] = ''
    df2['M'] = df2['B'].map(df2['B'].value_counts())
    utils.write_excel(file_path, out_file_name, '2_Y5TOP5_COUNT', df2.sort_values(by='A'))

    # TODO
    year_current = r'{}'.format(target_year)
    year_current_1 = r'{}'.format(target_year - 1)
    year_current_2 = r'{}'.format(target_year - 2)
    year_current_3 = r'{}'.format(target_year - 3)
    year_current_4 = r'{}'.format(target_year - 4)
    year_current_5 = r'{}'.format(target_year - 5)
    years = [year_current, year_current_1, year_current_2, year_current_3, year_current_4, year_current_5]
    # 计算第一名
    df_score_1 = __cal_score__(df2, years, 6)

    # 计算第二名
    df_score_2_5 = __cal_score__(df2, years[:5], 5)
    df_score_2_5_ = __cal2_score__(df_score_2_5, years[:4], '5-')
    df_score_2 = pd.concat([df_score_2_5[df_score_2_5['M'].eq(5)], df_score_2_5_])
    df_score_2_other = pd.concat([df_score_2_5, df_score_2]).drop_duplicates(keep=False)
    df_score_2_other['M'] = '3-'

    # 计算第三名
    df_score_3_4 = __cal_score__(df2, years[:4], 4)
    df_score_3_4_ = __cal2_score__(df_score_3_4, years[:3], '4-')
    df_score_3 = pd.concat([df_score_3_4[df_score_3_4['M'].eq(4)], df_score_3_4_])

    # 计算第四名
    df_score_4 = __cal_score__(df2, years[:3], 3)
    # 过滤3-
    df_score_4 = df_score_4[~df_score_4['M'].eq('3-')]

    # 评分3以上
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, '2_Y5TOP5_RECOUNT', df_score)

    # 2. 均值(mean)[以B列分组计算G列的均值(mean)]
    df_score_1['N'] = df_score_1.groupby('B')['G'].transform('mean')
    df_score_2['N'] = df_score_2.groupby('B')['G'].transform('mean')
    df_score_3['N'] = df_score_3.groupby('B')['G'].transform('mean')
    df_score_4['N'] = df_score_4.groupby('B')['G'].transform('mean')
    df_score_2_other['N'] = df_score_2_other.groupby('B')['G'].transform('mean')
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, '3_Y5TOP5_MEAN', df_score)

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()
    df_score_3 = df_score_3.groupby('A').agg({col: 'first' for col in df_score_3.columns if col != 'A'}).reset_index()
    df_score_4 = df_score_4.groupby('A').agg({col: 'first' for col in df_score_4.columns if col != 'A'}).reset_index()
    df_score_2_other = df_score_2_other.groupby('A').agg({col: 'first' for col in df_score_2_other.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_2 = df_score_2.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_3 = df_score_3.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_4 = df_score_4.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_2_other = df_score_2_other.sort_values(by=['M', 'N'], ascending=[True, False])

    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, 'Y5TOP5', df_score)
    log.log(r'Y5TOP5 :: {}'.format(df_score))


def __cal_score__(df, years, score):
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_all = df[df['M'].eq(score)]
    df_score = df[df['M'].eq(score) & df['K'].str.contains('|'.join(years))]
    df_score['M'] = df_score['B'].map(df_score['B'].value_counts())
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_a = df_score[df_score['M'].eq(score)]
    # 1. 评分为6的，且K列不是连续近6年的是第1名-
    df_score_b = pd.concat([df_score_all, df_score_a]).drop_duplicates(keep=False)
    df_score_b['M'] = r'{}-'.format(score)
    df_score = pd.concat([df_score_a, df_score_b])

    return df_score


def __cal2_score__(df, years, score):
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_all = df[df['M'].eq(score)]
    df_score = df[df['M'].eq(score) & df['K'].str.contains('|'.join(years))]
    df_score['M'] = df_score['B'].map(df_score['B'].value_counts())
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score = df_score[df_score['M'].eq(len(years))]
    df_score = df_score_all[df_score_all['B'].isin(df_score['B'])]
    df_score['M'] = score

    return df_score


def __y2_top6__():
    df = __read_file__()

    """
    过滤数据。(近三年)
    """
    header_g = config.get('file.name.stock.mm.header.g_y2')
    header_h = ['实施']
    # TODO
    year_current = r'{}'.format(target_year + 1)
    year_current_1 = r'{}'.format(target_year)
    year_current_2 = r'{}'.format(target_year - 1)
    years = [year_current, year_current_1, year_current_2]
    df_filtered1 = df[df['E'].str.contains('|'.join(years)) & df['H'].isin(header_h)]  # dataframe
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df2 = df_filtered1[df_filtered1['G'] >= header_g + 1]  # series TODO 6
    # utils.write_excel(file_path, out_file_name, '1_Y2TOP6_3Y', df2.sort_values(by='A'))

    """
    计算
    """
    """
    1. 评分。
        1. 评分为3的，第一名
        2. 评分为2的，且K列包含连续近两年的是第二名
        3. M列，G列平均值排序(以B列分组计算G列的均值(mean))
    """
    df2['L'] = ''
    df2['M'] = df2['B'].map(df2['B'].value_counts())
    utils.write_excel(file_path, out_file_name, '2_Y2TOP6_COUNT', df2.sort_values(by='A'))

    # 1. 评分为3的，第一名
    df_score_1 = df2[df2['M'].eq(3)]

    # TODO
    # 2. 评分为2的，且K列包含连续近两年的是第二名
    year_current = r'{}'.format(target_year)
    year_current_1 = r'{}'.format(target_year - 1)
    years = [year_current, year_current_1]
    df_score_2 = df2[df2['M'].eq(2) & df2['K'].str.contains('|'.join(years))]
    df_score_2['M'] = df_score_2['B'].map(df_score_2['B'].value_counts())
    # 2. 评分为2的，且K列包含连续近两年的是第二名
    df_score_2 = df_score_2[df_score_2['M'].eq(2)]

    # 3. 以B列分组计算G列的均值(mean)
    df_score_1['N'] = df_score_1.groupby('B')['G'].transform('mean')
    df_score_2['N'] = df_score_2.groupby('B')['G'].transform('mean')
    utils.write_excel(file_path, out_file_name, '3_Y2TOP6_MEAN', pd.concat([df_score_1, df_score_2]).sort_values(by='M'))

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by='N', ascending=False)
    df_score_2 = df_score_2.sort_values(by='N', ascending=False)

    df_score = pd.concat([df_score_1, df_score_2])
    utils.write_excel(file_path, out_file_name, 'Y2TOP6', df_score)
    log.log(r'Y2TOP6 :: {}'.format(df_score))


def __y10_top3__():

    df = __read_file__()

    """
    过滤数据。(近10年)
    """
    header_g = config.get('file.name.stock.mm.header.g_y10')
    header_h = ['实施']
    # TODO
    year_current = r'{}'.format(target_year + 1)
    year_current_1 = r'{}'.format(target_year)
    year_current_2 = r'{}'.format(target_year - 1)
    year_current_3 = r'{}'.format(target_year - 2)
    year_current_4 = r'{}'.format(target_year - 3)
    year_current_5 = r'{}'.format(target_year - 4)
    year_current_6 = r'{}'.format(target_year - 5)
    year_current_7 = r'{}'.format(target_year - 6)
    year_current_8 = r'{}'.format(target_year - 7)
    year_current_9 = r'{}'.format(target_year - 8)
    years = [year_current, year_current_1, year_current_2, year_current_3, year_current_4, year_current_5,
             year_current_6, year_current_7, year_current_8, year_current_9]
    df_filtered1 = df[df['E'].str.contains('|'.join(years)) & df['H'].isin(header_h)]  # dataframe
    df_filtered1['G'] = df_filtered1['G'].astype(float)
    df2 = df_filtered1[df_filtered1['G'] >= header_g]  # series TODO 3
    # utils.write_excel(file_path, out_file_name, '1_Y10TOP3_10Y', df2.sort_values(by='A'))

    """
    计算
    """

    """
    1. 评分。
    2. 均值(mean)。
    """
    # 1. 评分
    df2['L'] = ''
    df2['M'] = df2['B'].map(df2['B'].value_counts())
    utils.write_excel(file_path, out_file_name, '2_Y10TOP3_COUNT', df2.sort_values(by='A'))

    # TODO
    year_current = r'{}'.format(target_year)
    year_current_1 = r'{}'.format(target_year - 1)
    year_current_2 = r'{}'.format(target_year - 2)
    year_current_3 = r'{}'.format(target_year - 3)
    year_current_4 = r'{}'.format(target_year - 4)
    year_current_5 = r'{}'.format(target_year - 5)
    year_current_6 = r'{}'.format(target_year - 6)
    year_current_7 = r'{}'.format(target_year - 7)
    year_current_8 = r'{}'.format(target_year - 8)
    year_current_9 = r'{}'.format(target_year - 9)
    years = [year_current, year_current_1, year_current_2, year_current_3, year_current_4, year_current_5,
             year_current_6, year_current_7, year_current_8, year_current_9]
    # 计算第一名
    df_score_1 = __cal_score__(df2, years, 10)

    # 计算第二名
    df_score_2_5 = __cal_score__(df2, years[:9], 9)
    df_score_2_3 = __cal2_score__(df_score_2_5, years[:8], '9-')
    df_score_2 = pd.concat([df_score_2_5[df_score_2_5['M'].eq(9)], df_score_2_3])
    df_score_2_other = pd.concat([df_score_2_5, df_score_2]).drop_duplicates(keep=False)
    df_score_2_other['M'] = '7-'

    # 计算第三名
    df_score_3_5 = __cal_score__(df2, years[:8], 8)
    df_score_3_3 = __cal2_score__(df_score_3_5, years[:7], '8-')
    df_score_3 = pd.concat([df_score_3_5[df_score_3_5['M'].eq(8)], df_score_3_3])

    # 计算第四名
    df_score_4 = __cal_score__(df2, years[:7], 7)
    # 过滤3-
    df_score_4 = df_score_4[~df_score_4['M'].eq('7-')]

    # 评分3以上
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, '2_Y10TOP3_RECOUNT', df_score)

    # 2. 均值(mean)[以B列分组计算G列的均值(mean)]
    df_score_1['N'] = df_score_1.groupby('B')['G'].transform('mean')
    df_score_2['N'] = df_score_2.groupby('B')['G'].transform('mean')
    df_score_3['N'] = df_score_3.groupby('B')['G'].transform('mean')
    df_score_4['N'] = df_score_4.groupby('B')['G'].transform('mean')
    df_score_2_other['N'] = df_score_2_other.groupby('B')['G'].transform('mean')
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, '3_Y10TOP3_MEAN', df_score)

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()
    df_score_3 = df_score_3.groupby('A').agg({col: 'first' for col in df_score_3.columns if col != 'A'}).reset_index()
    df_score_4 = df_score_4.groupby('A').agg({col: 'first' for col in df_score_4.columns if col != 'A'}).reset_index()
    df_score_2_other = df_score_2_other.groupby('A').agg({col: 'first' for col in df_score_2_other.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_2 = df_score_2.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_3 = df_score_3.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_4 = df_score_4.sort_values(by=['M', 'N'], ascending=[True, False])
    df_score_2_other = df_score_2_other.sort_values(by=['M', 'N'], ascending=[True, False])

    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, 'Y10TOP3', df_score)
    log.log(r'Y10TOP3 :: {}'.format(df_score))


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)
