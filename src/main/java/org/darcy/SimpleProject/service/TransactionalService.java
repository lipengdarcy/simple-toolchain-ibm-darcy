package org.darcy.SimpleProject.service;

import javax.annotation.Resource;

import org.darcy.SimpleProject.dao.DeviceMapper;
import org.darcy.SimpleProject.model.Device;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务测试
 * 
 * @author lipeng
 */

@Service
public class TransactionalService {

	@Resource
	DeviceMapper DeviceMapper;

	public Device get(Integer id) {
		Device a = DeviceMapper.selectByPrimaryKey(id);
		return a;
	}

	@Transactional
	public int update1() {
		Device a = new Device();
		a.setDeviceName("@Transactional， 不会进入数据库");
		DeviceMapper.insertSelective(a);

		// 抛出异常，测试事务是否生效
		int exception = 1 / 0;
		System.out.println(exception);

		Device b = new Device();
		b.setDeviceName("b不不不设备");
		DeviceMapper.insertSelective(b);
		return 0;
	}

	public int update() {
		Device a = new Device();
		a.setDeviceName("这个数据是会进入数据库的，因为没有事务控制");
		DeviceMapper.insertSelective(a);

		// 抛出异常，测试事务是否生效
		int exception = 1 / 0;
		System.out.println(exception);

		Device b = new Device();
		b.setDeviceName("b设备");
		DeviceMapper.insertSelective(b);
		return 0;
	}

}
