package nino.wordutor.service;

import nino.wordutor.dao.VocabularyDAO;
import nino.wordutor.model.ChineseTranslation;
import nino.wordutor.model.ExampleSentence;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;
import nino.wordutor.util.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VocabularyService {

    //候选词生成规则——创建时间倒序
    final static public String TYPE_TIME = "TIME";
    //候选词生成规则——根据记忆等级倒序
    final static public String TYPE_LEVEL = "LEVEL";

    @Resource
    VocabularyDAO vocabularyDAO;


    public void save(Vocabulary vocabulary, List<ChineseTranslationVO> chineseTranslationVOList,
                     List<ExampleSentence> exampleSentenceList) {
        List<ChineseTranslation> chineseTranslations = new ArrayList<>();
        //过滤掉翻译为空
        for (ChineseTranslationVO vo : chineseTranslationVOList) {
            if (null != vo.getText() && vo.getText().trim().length() > 0) {
                ChineseTranslation chineseTranslation = new ChineseTranslation();
                chineseTranslation.setProperty(vo.getProperty());
                chineseTranslation.setText(vo.getText().replaceAll(";", ",")
                        .replaceAll("，", ",")
                        .replaceAll("；", ",").split(","));
                chineseTranslations.add(chineseTranslation);
            }
        }
        if (chineseTranslations.size() > 0) {
            vocabulary.setChineseTranslation(chineseTranslations);
        }

        if (null != exampleSentenceList) {
            //过滤掉空的
            List<ExampleSentence> exampleSentences = exampleSentenceList.stream().filter(
                    s -> StringUtils.isNotEmpty(s.getSentence()) && StringUtils.isNotEmpty(s.getTranslation()))
                    .collect(Collectors.toList());
            if (null != exampleSentences && exampleSentences.size() > 0) {
                vocabulary.setExampleSentences(exampleSentences);
            }
        }

        if (vocabulary.getId() == null) {
            IdWorker idWorker = new IdWorker(1, 1, 1);
            vocabulary.setId(idWorker.nextId());
            vocabulary.setCreateTime(new Date());
            vocabularyDAO.insert(vocabulary);
        }
        else {
            vocabulary.setUpdateTime(new Date());
            vocabularyDAO.update(vocabulary);
        }

    }

    public Vocabulary findOneByEnglish(String english) {
        return vocabularyDAO.findOneByEnglish(english);
    }

    public List<Vocabulary> findByEnglish(String text) {
        List<Vocabulary> vocabularyList = vocabularyDAO.findByEnglish(text);
        vocabularyList.sort((o1, o2) -> o1.getEnglish().length() > o2.getEnglish().length() ? 1
                : o1.getEnglish().length() == o2.getEnglish().length() ? 0 : -1);
        return vocabularyList;
    }

    public List<Vocabulary> findByChinese(String text) {
        return vocabularyDAO.findByChinese(text);
    }

    public List<Vocabulary> findByEnglishOrChinese(String text) {
        return vocabularyDAO.findByEnglishOrChineseTranslation(text);
    }

    public List<Vocabulary> findAll() {
        return vocabularyDAO.findAll();
    }

    public List<Vocabulary> findAllSortByCreateTime(int lines) {
        return vocabularyDAO.findAllSortByCreateTime(lines);
    }

    public List<Vocabulary> findAllSortByMemoryLevel(int lines) {
        return vocabularyDAO.findAllSortByMemoryLevel(lines);
    }

    public void plusMemoryLevel(Vocabulary vocabulary) {
        int memoryLevel = null == vocabulary.getMemoryLevel() ? 0 : vocabulary.getMemoryLevel();
        memoryLevel++;
        vocabulary.setMemoryLevel(memoryLevel);
        vocabularyDAO.update(vocabulary);
    }

    /**
     * 获取背或拼的候选词。
     * @param type 获取候选词规则。TIME: 根据创建时间倒序。LEVEL：根据记忆等级倒序
     * @param limit 获取数量
     * @return
     */
    public List<Vocabulary> getCandidateList(String type, int limit) {
        if (TYPE_LEVEL.equalsIgnoreCase(type)) {
            return vocabularyDAO.findAllSortByMemoryLevel(limit);
        } else if (TYPE_TIME.equalsIgnoreCase(type)) {
            return vocabularyDAO.findAllSortByCreateTime(limit);
        }
        return null;
    }
}
