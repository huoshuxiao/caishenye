from com.sun.caishenye.cube.common import utils
from com.sun.caishenye.cube.config import config, log

file_path = config.get('file.path')
file_name = config.get('file.name.stock.mm')
headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K']
header_h = ['预案']
header_g = '4'


def y_current():
    df = __read_file()
    log.log(df)

    # df_filtered = df[df['E'].str.contains(utils.year()) & df['H'].isin(header_h) & df['G'] > 5]
    df_filtered1 = df[df['E'].str.contains(utils.year()) & df['H'].isin(header_h)]
    df_filtered2 = df_filtered1[df_filtered1['G'] >= header_g]
    log.log(df_filtered2)

    df_sort = df_filtered2.sort_values(by='G', ascending=False)
    log.log(df_sort)
    utils.write_excel(file_path, 'mm', df_sort)


def y5_top5():
    df = __read_file()


def y2_top6():
    df = __read_file()


def __read_file():
    return utils.read_csv(file_path, file_name, headers)

