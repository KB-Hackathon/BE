# Dockerfile
FROM openjdk:17

# ---- 폰트/도구 설치 (패키지 매니저 자동 감지) ----
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
    fi; \
    \
    mkdir -p /usr/local/share/fonts/NotoSansCJKkr /usr/local/share/fonts/NanumGothic; \
    # Noto Sans CJK KR (공식 ZIP)
    wget -O /tmp/NotoSansCJKkr.zip "https://noto-website-2.storage.googleapis.com/pkgs/NotoSansCJKkr-hinted.zip"; \
    unzip -o /tmp/NotoSansCJKkr.zip -d /usr/local/share/fonts/NotoSansCJKkr; \
    rm -f /tmp/NotoSansCJKkr.zip; \
    # Nanum Gothic (GitHub 릴리스)
    wget -O /tmp/Nanum.zip "https://github.com/naver/nanumfont/releases/latest/download/NanumFont_TTF_ALL.zip"; \
    unzip -o /tmp/Nanum.zip "NanumGothic*.ttf" -d /usr/local/share/fonts/NanumGothic; \
    rm -f /tmp/Nanum.zip; \
    \
    # AppleSD/맑은고딕 -> Noto/Nanum 대체 매핑
    cat > /etc/fonts/local.conf <<'XML' \
<?xml version="1.0"?>
<!DOCTYPE fontconfig SYSTEM "urn:fontconfig:fonts.dtd">
<fontconfig>
  <alias>
    <family>Apple SD Gothic Neo</family>
    <prefer>
      <family>Noto Sans CJK KR</family>
      <family>NanumGothic</family>
    </prefer>
  </alias>
  <alias>
    <family>Malgun Gothic</family>
    <prefer>
      <family>Noto Sans CJK KR</family>
      <family>NanumGothic</family>
    </prefer>
  </alias>
</fontconfig>
XML
    ; \
    fc-cache -f -v

# ---- 앱 JAR ----
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

# 한글/UTF-8 기본
ENV JAVA_TOOL_OPTIONS="-Duser.language=ko -Duser.country=KR -Dfile.encoding=UTF-8"

ENTRYPOINT ["java","-jar","/app/app.jar"]
