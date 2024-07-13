package ${basePackage}.dal.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${basePackage}.dal.entities.${name};

/**
  * ${comment} Mapper
  *
  * @author ${author}
  * @since ${.now?string("yyyy-MM-dd")}
  **/
@Mapper
public interface ${javaClassName} extends BaseMapper<${name}> {
}