"""
十大股东
  1. 股东类型
  2. 非个人实体股东百分比
"""
import os

from com.sun.caishenye.cube.common import utils, consts
from com.sun.caishenye.cube.config import config, log

file_path = os.path.join(config.get('file.path'), consts.STOCK)
file_name = config.get('file.name.stock.ten_holder.file_name.in')
headers = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M']
out_file_name = config.get('file.name.stock.ten_holder.file_name.out')

execute_flg = config.get('file.name.stock.ten_holder.run')
origins = config.get('file.name.stock.ten_holder.origins')
ignores = config.get('file.name.stock.ten_holder.ignores')


def run():
    if execute_flg is None:
        return

    utils.remove_file(file_path, out_file_name)

    __cal__()


def __cal__():
    df = __read_file__()

    df['D'] = df['D'].astype(str)
    df['E'] = df['E'].astype(str)
    df['F'] = df['F'].astype(str)
    df['G'] = df['G'].astype(str)
    df['H'] = df['H'].astype(str)
    df['I'] = df['I'].astype(str)
    df['J'] = df['J'].astype(str)
    df['K'] = df['K'].astype(str)
    df['L'] = df['L'].astype(str)
    df['M'] = df['M'].astype(str)

    df['N'] = ''

    # 股东类型
    df = df.apply(__cal_type__, axis=1)

    # 股东百分比
    df = df.apply(__cal_ratio__, axis=1)

    """
    写excel
    """
    utils.write_excel(file_path, r'{}'.format(out_file_name), 'DEMO', df)
    log.log(r'{} :: {}'.format(r'{}_{}'.format(out_file_name, 'DEMO'), df))


# 非个人实体股东百分比
def __cal_ratio__(row):
    # log.log(row['B'])

    entities = [row['O'], row['P'], row['Q'], row['R'], row['S'], row['T'], row['U'], row['V'], row['W'], row['X']]
    total_entities = len(entities)
    # sum(True)
    person_entities = sum(__is_origin2__(entity) for entity in entities)
    # 显示小数点后2位
    row['N'] = r'{:.0%}'.format(person_entities / total_entities)
    return row


# 非个人实体
def __is_origin2__(entity):
    return entity


# 股东类型
def __cal_type__(row):
    # log.log(row['B'])

    row['O'] = __is_origin__(row['D'])
    row['P'] = __is_origin__(row['E'])
    row['Q'] = __is_origin__(row['F'])
    row['R'] = __is_origin__(row['G'])
    row['S'] = __is_origin__(row['H'])
    row['T'] = __is_origin__(row['I'])
    row['U'] = __is_origin__(row['J'])
    row['V'] = __is_origin__(row['K'])
    row['W'] = __is_origin__(row['L'])
    row['X'] = __is_origin__(row['M'])
    return row


# 非个人实体
def __is_origin__(cell):
    # log.log(cell)
    if len(cell) == 0:
        return False

    if len(ignores) > 0:
        if any(keyword in cell for keyword in ignores):
            # log.log(cell)
            return False

    # any() 函数在找到第一个匹配项时就会返回 True，否则返回 False
    return any(keyword.lower() in cell.lower() for keyword in origins)


def __read_file__():
    return utils.read_csv(file_path, file_name, headers)
