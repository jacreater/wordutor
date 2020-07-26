package nino.wordutor.dao;

import nino.wordutor.model.Vocabulary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class  VocabularyDAO {

    @Resource
    private MongoTemplate mongoTemplate;

    public void insert(Vocabulary vocabulary) {
        mongoTemplate.insert(vocabulary);
    }


    public void update(Vocabulary vocabulary) {
        mongoTemplate.remove(query(where("id").is(vocabulary.getId())), Vocabulary.class);
        mongoTemplate.insert(vocabulary);
    }

    public Vocabulary findOneByEnglish(String english) {
        return mongoTemplate.findOne(query(where("english").is(english)), Vocabulary.class);
    }

    public List<Vocabulary> findByEnglish(String text) {
        String regex = String.format("%s%s%s", "^.*", text, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return mongoTemplate.find(query(where("english").regex(pattern)), Vocabulary.class);
    }

    public List<Vocabulary> findByChinese(String text) {
        String regex = String.format("%s%s%s", "^.*", text, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return mongoTemplate.find(query(where("chineseTranslation.text").regex(pattern)), Vocabulary.class);
    }

    public List<Vocabulary> findByEnglishOrChineseTranslation(String text) {
        String regex = String.format("%s%s%s", "^.*", text, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("english").regex(pattern), Criteria.where("chineseTranslation.text").regex(pattern));
        return mongoTemplate.find(query(criteria), Vocabulary.class);
    }

    public List<Vocabulary> findAll() {
        return mongoTemplate.findAll(Vocabulary.class);
    }

    public void updateMemoryLevelById(Vocabulary vocabulary) {
        mongoTemplate.updateFirst(query(where("id").is(vocabulary.getId())),
                Update.update("memoryLevel", vocabulary.getMemoryLevel()), Vocabulary.class);
    }

    public List<Vocabulary> findAllSortByCreateTime(int limit) {
        return findAllSortByCreateTime(limit, null, null);
    }

    public List<Vocabulary> findAllSortByCreateTime(int limit, Date start, Date end) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("createTime")));
        query.limit(limit);
        if (null != start && null != end) {
            Criteria criteria = new Criteria();
            criteria.andOperator(Criteria.where("createTime").gt(start).lt(end));
            query.addCriteria(criteria);
        }
        return mongoTemplate.find(query, Vocabulary.class);
    }

    public List<Vocabulary> findAllSortByMemoryLevel(int limit, Date start, Date end) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("memoryLevel")));
        query.limit(limit);
        if (null != start && null != end) {
            Criteria criteria = new Criteria();
            criteria.andOperator(Criteria.where("createTime").gt(start).lt(end));
            query.addCriteria(criteria);
        }
        return mongoTemplate.find(query, Vocabulary.class);
    }

}
