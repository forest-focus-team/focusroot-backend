# syntax=docker/dockerfile:1
# ─────────────────────────────────────────────────────────────
# FocusRoot Backend — multi-stage image
#   Stage 1 (build):  biên dịch + đóng gói fat JAR bằng Maven (JDK 17)
#   Stage 2 (runtime): chạy trên JRE 17 alpine nhẹ, user non-root
# Build:  docker build -t focusroot-backend:latest .
# Run:    docker run --rm -p 8080:8080 -e JWT_SECRET=... focusroot-backend:latest
# ─────────────────────────────────────────────────────────────

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Tải dependency trước (tận dụng cache layer khi pom.xml không đổi)
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

# Biên dịch + đóng gói (test đã chạy ở CI, skip để build image nhanh)
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Stage 2: runtime ----
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Chạy bằng user không phải root
RUN addgroup -S focusroot && adduser -S focusroot -G focusroot

# Chỉ mang theo fat JAR (không lôi Maven/JDK/source vào image runtime)
COPY --from=build /build/target/*.jar app.jar
RUN chown -R focusroot:focusroot /app
USER focusroot

EXPOSE 8080

# JWT_SECRET BẮT BUỘC truyền vào lúc chạy (không có default) — app fail-fast nếu thiếu.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
