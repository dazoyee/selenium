REM start.bat  code
REM bin配下にある任意の.jarを設定すること
java^
    -javaagent:.\elastic-apm-agent-1.21.0.jar^
    -Delastic.apm.service_name=selenium^
    -Delastic.apm.server_urls=http://localhost:8200^
    -Delastic.apm.secret_token=<my token>^
    -Delastic.apm.environment=production^
    -Delastic.apm.application_packages=com.github.ioridazo^
    -Xms2G^
    -Xmx4G^
    -Duser.timezone=Asia/Tokyo^
    -jar selenium-<version>.jar
