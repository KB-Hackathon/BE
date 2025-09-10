# Dockerfile
FROM openjdk:17

# 1) fontconfig + 도구
RUN set -eux; \
    if command -v microdnf >/dev/null 2>&1; then \
        microdnf install -y fontconfig wget unzip && microdnf clean all; \
    elif command -v dnf >/dev/null 2>&1; then \
        dnf install -y fontconfig wget unzip && dnf clean all; \
    elif command -v yum >/dev/null 2>&1; then \
        yum install -y fontconfig wget unzip && yum clean all; \
    elif command -v apk >/dev/null 2>&1; then \
        apk add --no-cache fontconfig wget unzip; \
    elif command -v apt-get >/dev/null 2>&1; then \
        apt-get update && apt-get install -y --no-install-recommends fontconfig wget unzip && rm -rf /var/lib/apt/lists/*; \
    else \
        echo "No supported package manager found" >&2; exit 1; \
    fi

# 2) Noto Sans CJK KR 설치(공식 ZIP)
RUN set -eux; \
    mkdir -p /usr/local/share/fonts/NotoSansCJKkr; \
    wget -O /tmp/NotoSansCJKkr.zip "https://noto-website-2.storage.googleapis.com/pkgs/NotoSansCJKkr-hinted.zip"; \
    unzip -o /tmp/NotoSansCJKkr.zip -d /usr/local/share/fonts/NotoSansCJKkr; \
    rm -f /tmp/NotoSansCJKkr.zip

# 3) NanumGothic 설치(여러 URL 폴백; 전부 실패해도 빌드 계속)
RUN set -eux; \
    mkdir -p /usr/local/share/fonts/NanumGothic; \
    NANO_ZIP=/tmp/Nanum.zip; \
    success=0; \
    for u in \
      "https://github.com/naver/nanumfont/releases/latest/download/NanumFont_TTF_ALL.zip" \
      "https://cdn.jsdelivr.net/gh/naver/nanumfont@latest/NanumFont_TTF_ALL.zip" \
      "https://hangeul.naver.com/hangeul_static/font/NanumFont_TTF_ALL.zip" \
      "http://cdn.naver.com/naver/NanumFont/fontfiles/NanumFont_TTF_ALL.zip" \
    ; do \
      echo "TRY -> $u"; \
      if wget -O "$NANO_ZIP" "$u"; then success=1; break; fi; \
    done; \
    if [ "$success" -eq 1 ] && [ -s "$NANO_ZIP" ]; then \
      unzip -o "$NANO_ZIP" "NanumGothic*.ttf" -d /usr/local/share/fonts/NanumGothic; \
    else \
      echo "WARN: Nanum download failed. Proceeding with Noto only."; \
    fi; \
    rm -f "$NANO_ZIP"

# 4) AppleSD / Malgun → Noto/Nanum 대체 매핑
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
'</fontconfig>' > /etc/fonts/local.conf

# 5) 폰트 캐시
RUN fc-cache -f -v

# 6) 앱 JAR
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

# 한글/UTF-8 기본
ENV JAVA_TOOL_OPTIONS="-Duser.language=ko -Duser.country=KR -Dfile.encoding=UTF-8"

ENTRYPOINT ["java","-jar","/app/app.jar"]
