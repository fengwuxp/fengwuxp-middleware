<img src="./docs/logo.svg">

### 文档参见

- [Wind](https://www.yuque.com/suiyuerufeng-akjad/wind/frg18n25vz07swqu)

### Wind Archetype 发布

- [Maven Archetype生成项目模板](https://cloud.tencent.com/developer/article/1875305)
  发布命令

```shell
 cd wind-archetype && mvn archetype:create-from-project -Darchetype.properties=./archetype.properties
 cd ./target/generated-sources/archetype
 mvn install
 mvn deploy
```