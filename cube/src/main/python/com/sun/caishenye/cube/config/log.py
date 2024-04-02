import logging.config
import yaml
from com.sun.caishenye.cube.common import utils, consts
from com.sun.caishenye.cube.config import config as app_config

# 加载 YAML 配置文件
with open(utils.resources_path() + '/logging.yaml', 'r') as file:
    config = yaml.safe_load(file)

# 配置 logging 模块
logging.config.dictConfig(config)

# 创建日志记录器
logger = logging.getLogger(consts.APP_ID)


def log(message):
    debug = app_config.get('debug')
    if debug:
        logger.info(message)
    else:
        logger.debug(message)
