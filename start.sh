#!/bin/bash

# 应用名称
APP_NAME="demo-1.0.0.jar"
# 应用路径
APP_PATH="/opt/brakedisc"
# 日志文件路径
LOG_PATH="${APP_PATH}/logs"
# PID 文件
PID_FILE="${APP_PATH}/app.pid"
# 日志文件
LOG_FILE="${LOG_PATH}/app.log"

# 创建日志目录
mkdir -p "${LOG_PATH}"

# 检查 jar 包是否存在
if [ ! -f "${APP_PATH}/${APP_NAME}" ]; then
    echo "错误: ${APP_PATH}/${APP_NAME} 不存在！"
    exit 1
fi

# 获取 PID
get_pid() {
    if [ -f "${PID_FILE}" ]; then
        PID=$(cat "${PID_FILE}")
        if [ -n "${PID}" ]; then
            # 检查进程是否真的存在
            if ps -p "${PID}" > /dev/null 2>&1; then
                echo "${PID}"
                return 0
            fi
        fi
    fi
    return 1
}

# 启动函数
start() {
    PID=$(get_pid)
    if [ -n "${PID}" ]; then
        echo "应用已经在运行中，PID: ${PID}"
        return 1
    fi

    echo "正在启动应用..."
    cd "${APP_PATH}" || exit 1

    # 后台运行，输出到日志文件
    nohup java -jar "${APP_NAME}" > "${LOG_FILE}" 2>&1 &
    PID=$!

    # 保存 PID
    echo "${PID}" > "${PID_FILE}"

    # 等待一下检查是否启动成功
    sleep 3
    if ps -p "${PID}" > /dev/null 2>&1; then
        echo "应用启动成功！PID: ${PID}"
        echo "日志文件: ${LOG_FILE}"
        echo "查看日志: tail -f ${LOG_FILE}"
    else
        echo "应用启动失败，请检查日志: ${LOG_FILE}"
        rm -f "${PID_FILE}"
        return 1
    fi
}

# 停止函数
stop() {
    PID=$(get_pid)
    if [ -z "${PID}" ]; then
        echo "应用没有在运行"
        return 0
    fi

    echo "正在停止应用，PID: ${PID}..."
    kill "${PID}"

    # 等待进程结束
    for i in {1..10}; do
        if ! ps -p "${PID}" > /dev/null 2>&1; then
            echo "应用已停止"
            rm -f "${PID_FILE}"
            return 0
        fi
        sleep 1
    done

    # 强制杀死
    echo "强制停止应用..."
    kill -9 "${PID}"
    rm -f "${PID_FILE}"
    echo "应用已强制停止"
}

# 重启函数
restart() {
    echo "正在重启应用..."
    stop
    sleep 2
    start
}

# 查看状态
status() {
    PID=$(get_pid)
    if [ -n "${PID}" ]; then
        echo "应用正在运行，PID: ${PID}"
        echo "日志文件: ${LOG_FILE}"
    else
        echo "应用没有在运行"
    fi
}

# 查看日志
logs() {
    if [ -f "${LOG_FILE}" ]; then
        tail -f "${LOG_FILE}"
    else
        echo "日志文件不存在: ${LOG_FILE}"
    fi
}

# 主函数
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    logs)
        logs
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status|logs}"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动应用"
        echo "  stop    - 停止应用"
        echo "  restart - 重启应用"
        echo "  status  - 查看状态"
        echo "  logs    - 查看日志"
        exit 1
        ;;
esac
