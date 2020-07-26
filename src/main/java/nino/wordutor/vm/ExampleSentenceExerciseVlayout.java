package nino.wordutor.vm;

import lombok.extern.slf4j.Slf4j;
import nino.wordutor.model.ExampleSentence;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.util.Inflector;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;


@Slf4j
public class ExampleSentenceExerciseVlayout extends Vlayout {

    private ExampleSentence exampleSentence;
    private Vocabulary vocabulary;

    public void setExampleSentence(ExampleSentence exampleSentence, Vocabulary vocabulary, String fontSize) {
        this.exampleSentence = exampleSentence;
        this.vocabulary = vocabulary;

        Div sentenceDiv = (Div) getFellow("sentenceDiv");
        Label translationLabel = (Label) getFellow("translationLabel");
        sentenceDiv.getChildren().clear();
        String sentence = exampleSentence.getSentence();
        sentence = sentence.replaceAll("\\.", " ")
                .replaceAll(",", " ")
                .replaceAll("\\?", " ")
                .replaceAll("!", " ");
        if (vocabulary.getEnglish().trim().split(" ").length == 1) {
            String origin = vocabulary.getEnglish();
            String plural = StringUtils.isBlank(vocabulary.getPlural()) ? Inflector.getInstance().pluralize(origin)
                    : vocabulary.getPlural();
            String pastTense = StringUtils.isBlank(vocabulary.getPastTense()) ? Inflector.getInstance().pastTenslize(origin)
                    : vocabulary.getPastTense();
            //双写最后字母+ed
            String doublePastTense = StringUtils.isBlank(vocabulary.getPastTense())
                    ? origin + origin.substring(origin.length() - 1, origin.length()) + "ed"
                    : vocabulary.getPastTense();
            String pastParticiple = StringUtils.isBlank(vocabulary.getPastParticiple()) ? Inflector.getInstance().pastTenslize(origin)
                    : vocabulary.getPastParticiple();
            String presentParticiple = StringUtils.isBlank(vocabulary.getPresentParticiple()) ? Inflector.getInstance().present(origin)
                    : vocabulary.getPresentParticiple();
            String thirdSingular = StringUtils.isBlank(vocabulary.getThirdSingular()) ? Inflector.getInstance().singularize(origin)
                    : vocabulary.getThirdSingular();

            String[] words = sentence.split(" ");
            for (String word : words) {
                if (word.equals(origin) || word.equals(plural)
                        || word.equals(pastTense)
                        || word.equals(pastParticiple)
                        || word.equals(doublePastTense)
                        || word.equals(presentParticiple)
                        || word.equals(thirdSingular)) {
                    Textbox input = new Textbox();
                    input.setStyle("margin-right:0.5rem; font-size:" + fontSize);
                    input.setSclass("spell_input");
                    input.setAttribute("answer", word);
                    sentenceDiv.appendChild(input);

                    input.addEventListener(Events.ON_OK, new EventListener<KeyEvent>() {
                        @Override
                        public void onEvent(KeyEvent keyEvent) throws Exception {
                            if (input.getValue().trim().equals(word)) {
                                input.addSclass("spell_input_correct");
                            }
                            else
                                input.addSclass("spell_input_incorrect");
                        }
                    });
                }
                else {
                    Label label = new Label();
                    label.setValue(word);
                    label.setStyle("margin-right:0.5rem; font-size:" + fontSize);
                    sentenceDiv.appendChild(label);
                }
            }
            translationLabel.setValue(exampleSentence.getTranslation());
        }
    }
}
