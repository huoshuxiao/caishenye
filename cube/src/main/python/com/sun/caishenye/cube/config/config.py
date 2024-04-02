import yaml

from com.sun.caishenye.cube.common import utils


def get(k: str):
    # 加载 YAML 配置文件
    with open(utils.resources_path() + '/application.yaml', 'r') as file:
        config = yaml.safe_load(file)

    result = ''
    if '.' in k:
        keys = k.split('.')
        for i in keys:
            result = config.get(i)
            config = result
    else:
        result = config.get(k)

    return result
