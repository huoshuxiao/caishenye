import pandas as pd

from com.sun.caishenye.cube.common import utils
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.fr.file_name.in')
out_file_name = config.get('file.name.stock.fr.file_name.out')

headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U']
target_year = config.get('file.name.stock.fr.target_year')
header_i = config.get('file.name.stock.fr.header.i')
header_j = config.get('file.name.stock.fr.header.j')
header_k = config.get('file.name.stock.fr.header.k')


def run():
    utils.remove_file(file_path, out_file_name)

    # __y__()
    # __q1__()
    # __h__()
    # __q3__()

    # __y_q1__()
    # __y_h__()
    # __y_q3__()

    __y2_q1__()
    __y2_h__()
    __y2_q3__()

    __y5__()
    __y5_i_1__()
    __y3_i_2__()


def __y5_i_1__():
    dataframes = []
    _years = list(range(target_year - 4, target_year + 1))
    _header_i = header_i[1]
    for year in _years:
        dataframes.append(__one_y_i__(year, _header_i))
    df = pd.concat(dataframes, ignore_index=True)
    """
    计算
    """

    """
    1. 评分。
    2. 均值(mean)。
    """
    # 1. 评分
    df['V'] = ''
    df['W'] = df['B'].map(df['B'].value_counts())
    utils.write_excel(file_path, out_file_name, r'{}Y5_COUNT'.format(_header_i), df.sort_values(by='A'))

    _years.reverse()
    years = [str(year) for year in _years]

    # 计算第一名
    df_score_1 = __cal_score__(df, years, 5)

    # 计算第二名
    df_score_2_4 = __cal_score__(df, years[:4], 4)
    df_score_2_4_ = __cal2_score__(df_score_2_4, years[:3], '4-')
    df_score_2 = pd.concat([df_score_2_4[df_score_2_4['W'].eq(4)], df_score_2_4_])
    df_score_2_other = pd.concat([df_score_2_4, df_score_2]).drop_duplicates(keep=False)
    df_score_2_other['W'] = '2-'

    # 计算第三名
    df_score_3_3 = __cal_score__(df, years[:3], 3)
    df_score_3_3_ = __cal2_score__(df_score_3_3, years[:2], '3-')
    df_score_3 = pd.concat([df_score_3_3[df_score_3_3['W'].eq(3)], df_score_3_3_])

    # 计算第四名
    df_score_4 = __cal_score__(df, years[:2], 2)
    # 过滤2-
    df_score_4 = df_score_4[~df_score_4['W'].eq('2-')]

    # 评分3以上
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, r'{}Y5_RECOUNT'.format(_header_i), df_score.sort_values(by=['W', 'A'],
                                                                                                        ascending=[False, True]))

    # 2. 均值(mean)[以B列分组计算G列的均值(mean)]
    df_score_1['X'] = df_score_1.groupby('B')['I'].transform('mean')
    df_score_2['X'] = df_score_2.groupby('B')['I'].transform('mean')
    df_score_3['X'] = df_score_3.groupby('B')['I'].transform('mean')
    df_score_4['X'] = df_score_4.groupby('B')['I'].transform('mean')
    df_score_2_other['X'] = df_score_2_other.groupby('B')['I'].transform('mean')
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, r'{}Y5_MEAN'.format(_header_i), df_score.sort_values(by=['W', 'X', 'A'],
                                                                                                     ascending=[False, False, True]))

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()
    df_score_3 = df_score_3.groupby('A').agg({col: 'first' for col in df_score_3.columns if col != 'A'}).reset_index()
    df_score_4 = df_score_4.groupby('A').agg({col: 'first' for col in df_score_4.columns if col != 'A'}).reset_index()
    df_score_2_other = df_score_2_other.groupby('A').agg({col: 'first' for col in df_score_2_other.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_2 = df_score_2.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_3 = df_score_3.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_4 = df_score_4.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_2_other = df_score_2_other.sort_values(by=['W', 'X'], ascending=[True, False])

    """
    写excel。
    """
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, r'{}Y5'.format(_header_i), df_score)
    log.log(r'{}Y5 :: {}'.format(_header_i, df_score))


def __y3_i_2__():
    dataframes = []
    _years = list(range(target_year - 2, target_year + 1))
    _header_i = header_i[2]
    for year in _years:
        dataframes.append(__one_y_i__(year, _header_i))
    df = pd.concat(dataframes, ignore_index=True)
    """
    计算
    """

    """
    1. 评分。
    2. 均值(mean)。
    """
    # 1. 评分
    df['V'] = ''
    df['W'] = df['B'].map(df['B'].value_counts())
    utils.write_excel(file_path, out_file_name, r'{}Y3_COUNT'.format(_header_i), df.sort_values(by='A'))

    _years.reverse()
    years = [str(year) for year in _years]

    # 计算第一名
    df_score_1 = __cal_score__(df, years, 3)

    # 计算第二名
    df_score_2_2 = __cal_score__(df, years[:2], 2)
    df_score_2_2_ = __cal2_score__(df_score_2_2, years[:1], '2-')
    df_score_2 = pd.concat([df_score_2_2[df_score_2_2['W'].eq(2)], df_score_2_2_])

    # 评分3以上
    df_score = pd.concat([df_score_1, df_score_2])
    utils.write_excel(file_path, out_file_name, r'{}Y3_RECOUNT'.format(_header_i),
                      df_score.sort_values(by=['W', 'A'], ascending=[False, True]))

    # 2. 均值(mean)[以B列分组计算G列的均值(mean)]
    df_score_1['X'] = df_score_1.groupby('B')['I'].transform('mean')
    df_score_2['X'] = df_score_2.groupby('B')['I'].transform('mean')
    df_score = pd.concat([df_score_1, df_score_2])
    utils.write_excel(file_path, out_file_name, r'{}Y3_MEAN'.format(_header_i),
                      df_score.sort_values(by=['W', 'X', 'A'], ascending=[False, False, True]))

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_2 = df_score_2.sort_values(by=['W', 'X'], ascending=[True, False])

    """
    写excel。
    """
    df_score = pd.concat([df_score_1, df_score_2])
    utils.write_excel(file_path, out_file_name, r'{}Y3'.format(_header_i), df_score)
    log.log(r'{}Y3 :: {}'.format(_header_i, df_score))


def __y5__():
    dataframes = []
    _years = list(range(target_year - 4, target_year + 1))
    for year in _years:
        dataframes.append(__one_y__(year))
    df = pd.concat(dataframes, ignore_index=True)
    """
    计算
    """

    """
    1. 评分。
    2. 均值(mean)。
    """
    # 1. 评分
    df['V'] = ''
    df['W'] = df['B'].map(df['B'].value_counts())
    utils.write_excel(file_path, out_file_name, 'Y5_COUNT', df.sort_values(by='A'))

    _years.reverse()
    years = [str(year) for year in _years]

    # 计算第一名
    df_score_1 = __cal_score__(df, years, 5)

    # 计算第二名
    df_score_2_4 = __cal_score__(df, years[:4], 4)
    df_score_2_4_ = __cal2_score__(df_score_2_4, years[:3], '4-')
    df_score_2 = pd.concat([df_score_2_4[df_score_2_4['W'].eq(4)], df_score_2_4_])
    df_score_2_other = pd.concat([df_score_2_4, df_score_2]).drop_duplicates(keep=False)
    df_score_2_other['W'] = '2-'

    # 计算第三名
    df_score_3_3 = __cal_score__(df, years[:3], 3)
    df_score_3_3_ = __cal2_score__(df_score_3_3, years[:2], '3-')
    df_score_3 = pd.concat([df_score_3_3[df_score_3_3['W'].eq(3)], df_score_3_3_])

    # 计算第四名
    df_score_4 = __cal_score__(df, years[:2], 2)
    # 过滤2-
    df_score_4 = df_score_4[~df_score_4['W'].eq('2-')]

    # 评分3以上
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, 'Y5_RECOUNT', df_score.sort_values(by=['W', 'A'], ascending=[False, True]))

    # 2. 均值(mean)[以B列分组计算G列的均值(mean)]
    df_score_1['X'] = df_score_1.groupby('B')['I'].transform('mean')
    df_score_2['X'] = df_score_2.groupby('B')['I'].transform('mean')
    df_score_3['X'] = df_score_3.groupby('B')['I'].transform('mean')
    df_score_4['X'] = df_score_4.groupby('B')['I'].transform('mean')
    df_score_2_other['X'] = df_score_2_other.groupby('B')['I'].transform('mean')
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, 'Y5_MEAN', df_score.sort_values(by=['W', 'X', 'A'], ascending=[False, False, True]))

    # 为了不影响列的顺序，以A列分组(不写reset_index B列缺失)
    df_score_1 = df_score_1.groupby('A').agg({col: 'first' for col in df_score_1.columns if col != 'A'}).reset_index()
    df_score_2 = df_score_2.groupby('A').agg({col: 'first' for col in df_score_2.columns if col != 'A'}).reset_index()
    df_score_3 = df_score_3.groupby('A').agg({col: 'first' for col in df_score_3.columns if col != 'A'}).reset_index()
    df_score_4 = df_score_4.groupby('A').agg({col: 'first' for col in df_score_4.columns if col != 'A'}).reset_index()
    df_score_2_other = df_score_2_other.groupby('A').agg({col: 'first' for col in df_score_2_other.columns if col != 'A'}).reset_index()

    df_score_1 = df_score_1.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_2 = df_score_2.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_3 = df_score_3.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_4 = df_score_4.sort_values(by=['W', 'X'], ascending=[True, False])
    df_score_2_other = df_score_2_other.sort_values(by=['W', 'X'], ascending=[True, False])

    """
    写excel。
    """
    df_score = pd.concat([df_score_1, df_score_2, df_score_3, df_score_4, df_score_2_other])
    utils.write_excel(file_path, out_file_name, 'Y5', df_score)
    log.log(r'Y5 :: {}'.format(df_score))


def __cal_score__(df, years, score):
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_all = df[df['W'].eq(score)]
    df_score = df[df['W'].eq(score) & df['F'].str.contains('|'.join(years))]
    df_score['W'] = df_score['B'].map(df_score['B'].value_counts())
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_a = df_score[df_score['W'].eq(score)]
    # 1. 评分为6的，且K列不是连续近6年的是第1名-
    df_score_b = pd.concat([df_score_all, df_score_a]).drop_duplicates(keep=False)
    df_score_b['W'] = r'{}-'.format(score)
    df_score = pd.concat([df_score_a, df_score_b])

    return df_score


def __cal2_score__(df, years, score):
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score_all = df[df['W'].eq(score)]
    df_score = df[df['W'].eq(score) & df['F'].str.contains('|'.join(years))]
    df_score['W'] = df_score['B'].map(df_score['B'].value_counts())
    # 1. 评分为6的，且K列包含连续近6年的是第1名
    df_score = df_score[df_score['W'].eq(len(years))]
    df_score = df_score_all[df_score_all['B'].isin(df_score['B'])]
    df_score['W'] = score

    return df_score


def __one_y_i__(year, _header_i):
    target_month = config.get('file.name.stock.fr.header.f.month')
    target_day = config.get('file.name.stock.fr.header.f.day')
    if 'y' not in target_month.keys() and 'y' not in target_day.keys():
        return

    header_f = __f__(year, target_month['y'], target_day['y'])
    """
    过滤数据。
    """
    df = __read_file__()
    df_filtered1 = df[df['F'].eq(header_f)]

    df_filtered1 = df_filtered1[(df_filtered1['I'] >= _header_i) & (df_filtered1['I'] < 100)]

    """
    Sort
    """
    df_result = df_filtered1.sort_values(by='I', ascending=False)
    return df_result


def __one_y__(year):
    target_month = config.get('file.name.stock.fr.header.f.month')
    target_day = config.get('file.name.stock.fr.header.f.day')
    if 'y' not in target_month.keys() and 'y' not in target_day.keys():
        return

    header_f = __f__(year, target_month['y'], target_day['y'])
    """
    过滤数据。
    """
    df = __read_file__()
    df_filtered1 = df[df['F'].eq(header_f)]

    df_filtered1 = df_filtered1[(df_filtered1['I'] >= header_i[0]) & (df_filtered1['I'] < 100)
                                & (df_filtered1['J'] >= header_j)
                                & (df_filtered1['K'] >= header_k)]

    """
    Sort
    """
    df_result = df_filtered1.sort_values(by='I', ascending=False)
    return df_result


def __y__(year=target_year):
    df_result = __one_y__(year)
    """
    写excel。
    """
    utils.write_excel(file_path, out_file_name, r'{}Y'.format(year), df_result)
    log.log(r'{}Y :: {}'.format(year, df_result))
    return df_result


def __q1__():
    target_month = config.get('file.name.stock.fr.header.f.month')
    target_day = config.get('file.name.stock.fr.header.f.day')
    if 'q1' not in target_month.keys() and 'q1' not in target_day.keys():
        return

    _target_year = target_year + 1
    header_f = __f__(_target_year, target_month['q1'], target_day['q1'])
    """
    过滤数据。
    """
    df = __read_file__()
    df_filtered1 = df[df['F'].eq(header_f)]

    df_filtered1 = df_filtered1[(df_filtered1['I'] >= header_i[0]) & (df_filtered1['I'] < 100)
                                & (df_filtered1['J'] >= header_j)
                                & (df_filtered1['K'] >= header_k)]

    """
    写excel。
    """
    df_result = df_filtered1.sort_values(by='I', ascending=False)
    utils.write_excel(file_path, out_file_name, r'{}Y{}'.format(_target_year, 'Q1'), df_result)
    log.log(r'{}Y{} :: {}'.format(_target_year, 'Q1', df_result))
    return df_result


def __h__():
    target_month = config.get('file.name.stock.fr.header.f.month')
    target_day = config.get('file.name.stock.fr.header.f.day')
    if 'h' not in target_month.keys() and 'h' not in target_day.keys():
        return

    _target_year = target_year + 1
    header_f = __f__(_target_year, target_month['h'], target_day['h'])
    """
    过滤数据。
    """
    df = __read_file__()
    df_filtered1 = df[df['F'].eq(header_f)]

    df_filtered1 = df_filtered1[(df_filtered1['I'] >= header_i[0]) & (df_filtered1['I'] < 100)
                                & (df_filtered1['J'] >= header_j)
                                & (df_filtered1['K'] >= header_k)]

    """
    写excel。
    """
    df_result = df_filtered1.sort_values(by='I', ascending=False)
    utils.write_excel(file_path, out_file_name, r'{}Y{}'.format(_target_year, 'H1'), df_result)
    log.log(r'{}Y{} :: {}'.format(_target_year, 'H1', df_result))
    return df_result


def __q3__():
    target_month = config.get('file.name.stock.fr.header.f.month')
    target_day = config.get('file.name.stock.fr.header.f.day')
    if 'q3' not in target_month.keys() and 'q3' not in target_day.keys():
        return

    _target_year = target_year + 1
    header_f = __f__(_target_year, target_month['q3'], target_day['q3'])
    """
    过滤数据。
    """
    df = __read_file__()
    df_filtered1 = df[df['F'].eq(header_f)]

    df_filtered1 = df_filtered1[(df_filtered1['I'] >= header_i[0]) & (df_filtered1['I'] < 100)
                                & (df_filtered1['J'] >= header_j)
                                & (df_filtered1['K'] >= header_k)]

    """
    写excel。
    """
    df_result = df_filtered1.sort_values(by='I', ascending=False)
    utils.write_excel(file_path, out_file_name, r'{}Y{}'.format(_target_year, 'Q3'), df_result)
    log.log(r'{}Y{} :: {}'.format(_target_year, 'Q3', df_result))
    return df_result


def __y_q1__():
    y_result = __y__()
    q1_result = __q1__()

    if y_result is None or q1_result is None:
        return

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(q1_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}{}'.format(target_year, target_year + 1, 'Q1'), df_result)
    log.log(r'{}Y{}{} :: {}'.format(target_year, target_year + 1, 'Q1', df_result))
    return df_result


def __y_h__():
    y_result = __y__()
    h_result = __h__()

    if y_result is None or h_result is None:
        return

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(h_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}{}'.format(target_year, target_year + 1, 'H'), df_result)
    log.log(r'{}Y{}{} :: {}'.format(target_year, target_year + 1, 'H', df_result))
    return df_result


def __y_q3__():
    y_result = __y__()
    q3_result = __q3__()

    if y_result is None or q3_result is None:
        return

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(q3_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}{}'.format(target_year, target_year + 1, 'Q3'), df_result)
    log.log(r'{}Y{}{} :: {}'.format(target_year, target_year + 1, 'Q3', df_result))
    return df_result


def __y2_q1__():
    y_q1_result = __y_q1__()

    if y_q1_result is None:
        return

    y_result = __y__(target_year - 1)

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(y_q1_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}Y{}Q1'.format(target_year - 1, target_year, target_year + 1), df_result)
    log.log(r'{}Y{}Y{}Q1 :: {}'.format(target_year - 1, target_year, target_year + 1, df_result))
    return df_result


def __y2_h__():
    y_h_result = __y_h__()

    if y_h_result is None:
        return

    y_result = __y__(target_year - 1)

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(y_h_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}Y{}H'.format(target_year - 1, target_year, target_year + 1), df_result)
    log.log(r'{}Y{}Y{}H :: {}'.format(target_year - 1, target_year, target_year + 1, df_result))
    return df_result


def __y2_q3__():
    y_q3_result = __y_q3__()

    if y_q3_result is None:
        return

    y_result = __y__(target_year - 1)

    """
    写excel。
    """
    df_result = y_result[y_result['B'].isin(y_q3_result['B'])]
    utils.write_excel(file_path, out_file_name, r'{}Y{}Y{}Q3'.format(target_year - 1, target_year, target_year + 1), df_result)
    log.log(r'{}Y{}Y{}Q3 :: {}'.format(target_year - 1, target_year, target_year + 1, df_result))
    return df_result


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)


def __f__(year, mm, dd):
    # mm/dd/yyyy
    mm = str(mm)
    if len(mm) == 1:
        mm = r'0{}'.format(mm)
    return r'{}-{}-{}'.format(year, mm, dd)
