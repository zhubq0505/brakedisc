#!/bin/bash

#================================================================================
# BrakeDisc Docker 部署脚本
# 功能：
#   1. 从 Git 仓库拉取最新代码
#   2. Maven 编译 JAR 包
#   3. 重启 Docker 服务
#================================================================================

set -e  # 遇到错误立即退出

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
PROJECT_DIR="/Users/zbq/brakedisc"
APP_NAME="brakedisc"
JAR_NAME="demo-1.0.0.jar"
DOCKER_IMAGE="${APP_NAME}:latest"
DOCKER_CONTAINER="${APP_NAME}-app"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查必要工具
check_tools() {
    log_info "检查必要工具..."
    
    local tools=("git" "mvn" "docker")
    for tool in "${tools[@]}"; do
        if ! command -v $tool &> /dev/null; then
            log_error "$tool 未安装，请先安装"
            exit 1
        fi
    done
    
    log_success "所有必要工具已安装"
}

# 1. 拉取最新代码
pull_code() {
    log_info "步骤 1/3: 从 Git 仓库拉取最新代码"
    
    cd "$PROJECT_DIR" || {
        log_error "无法切换到项目目录: $PROJECT_DIR"
        exit 1
    }
    
    # 检查是否是 Git 仓库
    if [ ! -d ".git" ]; then
        log_error "当前目录不是 Git 仓库"
        exit 1
    fi
    
    # 显示当前分支
    current_branch=$(git branch --show-current)
    log_info "当前分支: $current_branch"
    
    # 拉取最新代码
    log_info "正在拉取最新代码..."
    git fetch origin
    
    # 检查是否有更新
    LOCAL=$(git rev-parse @)
    REMOTE=$(git rev-parse @{u})
    
    if [ "$LOCAL" = "$REMOTE" ]; then
        log_warning "本地代码已是最新，无需更新"
    else
        log_info "发现更新，正在合并..."
        git pull origin "$current_branch"
        log_success "代码已更新到最新版本"
        
        # 显示提交信息
        log_info "最新提交:"
        git log -1 --oneline
    fi
}

# 2. Maven 编译 JAR 包
build_jar() {
    log_info "步骤 2/3: Maven 编译 JAR 包"
    
    cd "$PROJECT_DIR" || {
        log_error "无法切换到项目目录: $PROJECT_DIR"
        exit 1
    }
    
    # 检查 pom.xml 是否存在
    if [ ! -f "pom.xml" ]; then
        log_error "pom.xml 文件不存在"
        exit 1
    fi
    
    log_info "正在清理旧的构建文件..."
    mvn clean
    
    log_info "正在编译并打包..."
    mvn package -DskipTests
    
    # 检查 JAR 包是否生成
    if [ ! -f "target/$JAR_NAME" ]; then
        log_error "JAR 包构建失败: target/$JAR_NAME 不存在"
        exit 1
    fi
    
    log_success "JAR 包构建成功: target/$JAR_NAME"
}

# 3. 重启 Docker 服务
restart_docker() {
    log_info "步骤 3/3: 重启 Docker 服务"
    
    cd "$PROJECT_DIR" || {
        log_error "无法切换到项目目录: $PROJECT_DIR"
        exit 1
    }
    
    # 检查 Dockerfile 是否存在
    if [ ! -f "Dockerfile" ]; then
        log_error "Dockerfile 不存在"
        exit 1
    fi
    
    # 检查 docker-compose.yml 是否存在
    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 不存在"
        exit 1
    fi
    
    # 停止并删除旧容器
    log_info "停止并删除旧容器..."
    if docker ps -a --format '{{.Names}}' | grep -q "^${DOCKER_CONTAINER}$"; then
        docker stop "${DOCKER_CONTAINER}" 2>/dev/null || true
        docker rm "${DOCKER_CONTAINER}" 2>/dev/null || true
        log_success "旧容器已删除"
    else
        log_info "未发现运行中的容器"
    fi
    
    # 删除旧镜像
    log_info "删除旧镜像..."
    if docker images --format '{{.Repository}}:{{.Tag}}' | grep -q "^${DOCKER_IMAGE}$"; then
        docker rmi "${DOCKER_IMAGE}" 2>/dev/null || true
        log_success "旧镜像已删除"
    else
        log_info "未发现旧镜像"
    fi
    
    # 构建新镜像
    log_info "正在构建 Docker 镜像..."
    docker build -t "${DOCKER_IMAGE}" .
    
    if [ $? -ne 0 ]; then
        log_error "Docker 镜像构建失败"
        exit 1
    fi
    
    log_success "Docker 镜像构建成功: ${DOCKER_IMAGE}"
    
    # 启动服务
    log_info "正在启动 Docker 服务..."
    docker-compose up -d
    
    if [ $? -ne 0 ]; then
        log_error "Docker 服务启动失败"
        exit 1
    fi
    
    log_success "Docker 服务已启动"
    
    # 等待服务就绪
    log_info "等待服务就绪..."
    sleep 10
    
    # 显示容器状态
    log_info "容器状态:"
    docker ps --filter "name=${DOCKER_CONTAINER}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    # 显示日志（最后10行）
    log_info "应用日志（最后10行）:"
    docker logs --tail 10 "${DOCKER_CONTAINER}"
    
    log_success "部署完成！"
    log_info "应用访问地址: http://localhost:8080"
    log_info "H2 控制台: http://localhost:8080/h2-console"
}

# 显示帮助信息
show_help() {
    echo "BrakeDisc Docker 部署脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  all     - 执行完整部署流程（拉取代码 + 编译 + 重启 Docker）"
    echo "  pull    - 仅拉取最新代码"
    echo "  build   - 仅编译 JAR 包"
    echo "  restart - 仅重启 Docker 服务"
    echo "  status  - 查看 Docker 服务状态"
    echo "  logs    - 查看应用日志"
    echo "  stop    - 停止 Docker 服务"
    echo "  help    - 显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 all       # 完整部署"
    echo "  $0 pull      # 仅拉取代码"
    echo "  $0 status    # 查看状态"
}

# 查看状态
show_status() {
    log_info "Docker 服务状态:"
    docker ps --filter "name=${DOCKER_CONTAINER}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    if docker ps --filter "name=${DOCKER_CONTAINER}" --format "{{.Names}}" | grep -q "${DOCKER_CONTAINER}"; then
        log_info "容器运行状态: ✅ 运行中"
        log_info "应用访问地址: http://localhost:8080"
    else
        log_warning "容器运行状态: ❌ 未运行"
    fi
}

# 查看日志
show_logs() {
    if docker ps --filter "name=${DOCKER_CONTAINER}" --format "{{.Names}}" | grep -q "${DOCKER_CONTAINER}"; then
        log_info "实时日志（按 Ctrl+C 退出）:"
        docker logs -f "${DOCKER_CONTAINER}"
    else
        log_error "容器未运行，无法查看日志"
        exit 1
    fi
}

# 停止服务
stop_service() {
    log_info "停止 Docker 服务..."
    
    cd "$PROJECT_DIR" || {
        log_error "无法切换到项目目录: $PROJECT_DIR"
        exit 1
    }
    
    if [ -f "docker-compose.yml" ]; then
        docker-compose down
        log_success "Docker 服务已停止"
    else
        log_error "docker-compose.yml 不存在"
        exit 1
    fi
}

# 主函数
main() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  BrakeDisc Docker 部署脚本${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
    
    # 如果没有参数，显示帮助
    if [ $# -eq 0 ]; then
        show_help
        exit 0
    fi
    
    case "$1" in
        all)
            check_tools
            pull_code
            build_jar
            restart_docker
            ;;
        pull)
            check_tools
            pull_code
            ;;
        build)
            check_tools
            build_jar
            ;;
        restart)
            check_tools
            restart_docker
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs
            ;;
        stop)
            stop_service
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "未知选项: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
