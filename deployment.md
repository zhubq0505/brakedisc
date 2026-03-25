## 部署说明

### 1. Dockerfile 文件
- 使用多阶段构建优化镜像大小
- 基于 OpenJDK 8 运行时环境
- 包含健康检查机制
- 优化了缓存层以加快构建速度

### 2. docker-compose.yml 文件
- 定义了服务编排配置
- 配置了端口映射 (8080:8080)
- 实现了数据持久化 (data 和 logs 目录)
- 添加了健康检查和自动重启策略
- 配置了独立的网络环境

### 3. run.sh 脚本
实现了完整的自动化部署功能：

**主要功能：**
1. **拉取代码** - 从 Git 仓库拉取最新代码
2. **编译打包** - Maven 编译 JAR 包
3. **重启服务** - 停止旧容器，构建新镜像，启动服务

**脚本命令：**
```bash
./run.sh all      # 完整部署（拉取 + 编译 + 重启）
./run.sh pull     # 仅拉取代码
./run.sh build    # 仅编译打包
./run.sh restart  # 仅重启 Docker 服务
./run.sh status   # 查看服务状态
./run.sh logs     # 查看应用日志
./run.sh stop     # 停止服务
./run.sh help     # 显示帮助信息
```

### 4. .dockerignore 文件
- 排除不必要的文件以减小构建上下文
- 提高构建速度
- 保护敏感文件不被包含在镜像中

### 5. DOCKER_DEPLOY.md 文件
- 详细的部署文档
- 使用说明和示例
- 故障排查指南

## 使用方法

### 方式一：一键部署（推荐）
```bash
cd /Users/zbq/brakedisc
./run.sh all
```

### 方式二：分步执行
```bash
# 1. 拉取最新代码
./run.sh pull

# 2. 编译打包
./run.sh build

# 3. 重启服务
./run.sh restart
```

### 访问地址

部署成功后：
- **应用主页**: http://localhost:8080
- **H2 控制台**: http://localhost:8080/h2-console



## 测试验证

1. **测试部署**
   ```bash
   ./run.sh all
   ```

2. **验证服务**
   ```bash
   curl http://localhost:8080
   ```

3. **查看日志**
   ```bash
   ./run.sh logs
   ```
