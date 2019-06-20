package cn.itcast.log;

import cn.itcast.common.PageResult;
import cn.itcast.exception.ServiceException;
import cn.itcast.interceptor.CurrentUserHolder;
import cn.itcast.mapper.OperationLogMapper;
import cn.itcast.pojo.OperationLog;
import cn.itcast.pojo.User;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
@Slf4j
public class OperateAdvice {

	@Autowired
	private OperationLogMapper operationLogMapper;

	@Around("execution(* cn.itcast.controller.*.*(..)) && @annotation(operateLog)")
	public Object insertLogAround(ProceedingJoinPoint pjp , OperateLog operateLog) throws Throwable{
		OperationLog op = null;
		try {
			log.info(" *********************************** 记录日志 [start]  ****************************** ");

			op = new OperationLog();

			op.setOperateTime(new Date());

			User user = CurrentUserHolder.getUser();
			if(user !=null){
				op.setOperateUser(user.getId());
			}

			op.setOperateClass(pjp.getTarget().getClass().getName());
			op.setOperateMethod(pjp.getSignature().getName());

			Object[] args = pjp.getArgs();
			op.setParamAndValue(Arrays.toString(args));
		} catch (Exception e) {
			e.printStackTrace();
		}

		long start_time = System.currentTimeMillis();

		//放行
		Object object = null;
		try {
			object = pjp.proceed();
		} catch (Throwable throwable) {
			//记录错误日志
			//...

			throw new RuntimeException();
		}

		long end_time = System.currentTimeMillis();


		try {
			op.setCostTime(end_time - start_time);

			if(object != null){
				op.setReturnClass(object.getClass().getName());
				op.setReturnValue(object.toString());
			}else{
				op.setReturnClass("java.lang.Object");
				op.setParamAndValue("void");
			}

			log.info(JSON.toJSONString(op));

			operationLogMapper.insert(op);

			log.info(" *********************************** 记录日志 [end]  ****************************** ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}
	
}
