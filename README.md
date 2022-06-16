# 项目启动和部署演示
项目语言：Java，框架：springboot ; 无db依赖

部署步骤：

1. 本地maven编译、打包、生成目标jar包（qidian-demo-java.jar）
先maven clean后，然后执行maven install，生成目标jar文件

2. 在目标机器,进入/opt目录，创建java_demo目录，然后上传Dockerfile文件和qidian-demo-java.jar包
注意：把jar包放到target目录

3. 构建镜像，启动项目
```
cd /opt/java_demo
docker build . -t qidian_demo_java
```
执行上述命令生成镜像，查看镜像，docker images,启动 刚刚生成的镜像
```
docker images
docker run -d -p 8082:8082 刚刚的镜像ID
```