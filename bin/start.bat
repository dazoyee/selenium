:: 設定ファイルを読み込む
for /f "usebackq tokens=1,* delims==" %%a in ("env") do (
    set %%a=%%b
)

rem ELASTIC_APM_AGENT_PATH=
rem ELASTIC_APM_AGENT_VERSION=
rem SELENIUM_VERSION=

java^
    -javaagent:%ELASTIC_APM_AGENT_PATH%\elastic-apm-agent-%ELASTIC_APM_AGENT_VERSION%.jar  ^
    -Delastic.apm.service_name=selenium  ^
    -Delastic.apm.server_urls=http://localhost:8200  ^
    -Delastic.apm.secret_token= ^
    -Delastic.apm.environment=production  ^
    -Delastic.apm.application_packages=com.github.ioridazo  ^
    -Xms256m^
    -Xmx256m^
    -jar selenium-%SELENIUM_VERSION%.jar
