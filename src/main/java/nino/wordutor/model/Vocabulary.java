package nino.wordutor.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Vocabulary {

	private Long id;

	private String english;
	
	private String phonetic;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Date lastTestTime;
	
	private Integer lastTestResult;
	
	private Integer memoryLevel;

	private List<ChineseTranslation> chineseTranslation;

	private List<ExampleSentence> exampleSentences;

	//复数形式
	private String plural;

	//过去式
	private String pastTense;

	//过去分词
	private String pastParticiple;

	//现在分词
	private String presentParticiple;

	//第三人称单数
	private String thirdSingular;

	/**
	 * 用于排序时，忽略大小写
	 * @return
	 */
	public String getEnglishLowerCase() {
		return english.toLowerCase();
	}

}
