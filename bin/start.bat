REM start.bat  code
REM 配下のディレクトリにelastic-apm-agent.jarが設定あること
REM bin配下にある任意の.jarを設定すること
java^
    -javaagent:\path\to\elastic-apm-agent-1.21.0.jar^
    -Delastic.apm.service_name=selenium^
    -Delastic.apm.server_urls=http://localhost:8200^
    -Delastic.apm.secret_token=<my token>^
    -Delastic.apm.environment=production^
    -Delastic.apm.application_packages=com.github.ioridazo^
    -Xms256m^
    -Xmx256m^
    -jar selenium-<version>.jar
