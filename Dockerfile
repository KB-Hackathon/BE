# Dockerfile
FROM openjdk:17

# ---- 폰트 설치: fontconfig + Noto CJK + (가능하면) Nanum ----
# - fonts-nanum 패키지가 없을 경우 GitHub 릴리스에서 나눔고딕 TTF를 직접 추가
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends fontconfig fonts-noto-cjk wget unzip; \
    if ! apt-get install -y --no-install-recommends fonts-nanum; then \
      mkdir -p /usr/local/share/fonts/NanumGothic; \
      cd /usr/local/share/fonts/NanumGothic; \
      wget -O Nanum.zip "https://github.com/naver/nanumfont/releases/latest/download/NanumFont_TTF_ALL.zip"; \
      unzip -o Nanum.zip "NanumGothic*.ttf"; \
      rm -f Nanum.zip; \
    fi; \
    fc-cache -f -v; \
    rm -rf /var/lib/apt/lists/*

# ---- AppleSD/맑은고딕 => Noto/Nanum 대체 매핑 ----
RUN set -eux; \
    printf '%s\n' \
'<?xml version="1.0"?>' \
'<!DOCTYPE fontconfig SYSTEM "urn:fontconfig:fonts.dtd">' \
'<fontconfig>' \
'  <alias>' \
'    <family>Apple SD Gothic Neo</family>' \
'    <prefer>' \
'      <family>Noto Sans CJK KR</family>' \
'      <family>NanumGothic</family>' \
'    </prefer>' \
'  </alias>' \
'  <alias>' \
'    <family>Malgun Gothic</family>' \
'    <prefer>' \
'      <family>Noto Sans CJK KR</family>' \
'      <family>NanumGothic</family>' \
'    </prefer>' \
'  </alias>' \
'</fontconfig>' \
    > /etc/fonts/local.conf; \
    fc-cache -f -v

# ---- 앱 JAR ----
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

# 한글/UTF-8 기본
ENV JAVA_TOOL_OPTIONS="-Duser.language=ko -Duser.country=KR -Dfile.encoding=UTF-8"

ENTRYPOINT ["java", "-jar", "app.jar"]
