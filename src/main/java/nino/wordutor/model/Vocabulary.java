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


}
