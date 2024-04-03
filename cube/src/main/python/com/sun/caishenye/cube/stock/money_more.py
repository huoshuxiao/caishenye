from com.sun.caishenye.cube.common import utils
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.mm')
headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K']


def y_current():
    df = __read_file__()

    """
    过滤数据。
    """
    header_h = ['预案']
    header_g = config.get('file.name.stock.header.g')
    # df_filtered = df[df['E'].str.contains(utils.year()) & df['H'].isin(header_h) & df['G'] > 5]
    df_filtered1 = df[df['E'].str.contains(utils.year()) & df['H'].isin(header_h)]
    df_filtered2 = df_filtered1[df_filtered1['G'] >= header_g]

    """
    写excel。
    """
    df_sort = df_filtered2.sort_values(by='G', ascending=False)
    log.log(df_sort)
    utils.write_excel(file_path, 'mm', df_sort)


def y5_top5():
    df = __read_file__()


def y2_top6():
    df = __read_file__()

    """
    过滤数据。(近三年)
    """
    header_h = ['实施']
    header_g = '6'
    df_filtered1 = df[df['E'].str.contains(utils.year() | utils.year() - 1 | utils.year() - 2) & df['H'].isin(header_h)]  # dataframe
    df2 = df_filtered1[df_filtered1['G'] >= header_g]  # series

    """
    评分。(以A和E出现的次数评分，添加新列L)
        1.评分为3的，且E列包含连续近三年的是第一名
        2.评分为2的，且E列包含连续近两年的是第二名
    """
    df2['L'] = __score__(df2['E'].values, [utils.year(), utils.year() - 1, utils.year() - 2])
    log.log(df2)
    """
    排序。
        平均值。(G列的平均值，添加新列M)
    """
    df2['M'] = df2['G'].mean()
    log.log(df2)


def __score__(values, param):

    return 3


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)

