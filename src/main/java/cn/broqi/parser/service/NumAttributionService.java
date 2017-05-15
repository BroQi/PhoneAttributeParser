package cn.broqi.parser.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.broqi.parser.entity.NumAttribution;
import cn.broqi.parser.handler.NumberHttpHandler;
import cn.broqi.parser.respository.NumAttributionDao;

@Service
public class NumAttributionService {

	@Autowired
	private NumAttributionDao numAttributionDao;

	@Autowired
	private NumberHttpHandler numberHttpHandler;

	public NumAttribution findByNum(String num) {
		NumAttribution numAttribution = numAttributionDao.findOne(num.substring(0, 7));
		if (numAttribution == null) {
			numAttribution = numberHttpHandler.getFrominternet(num);
			this.insert(numAttribution);
		}
		return numAttribution;
	}

	@Transactional
	public NumAttribution insert(NumAttribution num) {
		return numAttributionDao.save(num);
	}

}
