# Backend

## 运行环境

- jdk17
- mongodb
``` bash
# windows版的可以平替
littlebug@DESKTOP-VS4788L:~$ mongo --version
MongoDB shell version v4.4.29
Build Info: {
    "version": "4.4.29",
    "gitVersion": "f4dda329a99811c707eb06d05ad023599f9be263",
    "openSSLVersion": "OpenSSL 1.1.1f  31 Mar 2020",
    "modules": [],
    "allocator": "tcmalloc",
    "environment": {
        "distmod": "ubuntu2004",
        "distarch": "x86_64",
        "target_arch": "x86_64"
    }
}
littlebug@DESKTOP-VS4788L:~$ mysql --version
mysql  Ver 8.0.42-0ubuntu0.22.04.1 for Linux on x86_64 ((Ubuntu))
```

## 运行准备

git clone 本项目

在mysql中创建 建数据库并选择使用。
执行[数据库初始化sql](../Design/DB/newSql.sql)
修改[后端配置文件](./Interview/src/main/resources/application.yml)中的`datasource`字段和`mongodb`字段
接口文档地址：
http://localhost:8080/swagger-ui/index.html/

用户功能业务流程后端

> todoList
- [x] 完成接收简历的第三方模块
- [x] create完成简历的分析
- [x] start开始调用ai生成问题