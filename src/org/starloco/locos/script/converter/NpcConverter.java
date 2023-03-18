package org.starloco.locos.script.converter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.starloco.locos.entity.npc.NpcAnswer;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.other.Action;
import org.starloco.locos.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcConverter implements ConverterInterface<NpcTemplate> {

    private final String BASE_DIRECTORY = "./scripts/npcs/untested/";
    private String newLine;

    public NpcConverter() {
        newLine = System.getProperty("line.separator");
    }

    public void convert() {

        for(NpcTemplate template : World.world.getNPCTemplates().values()) {
            if(template.legacy == null) continue;
            System.out.println(template.getId());
            final StringBuilder content = new StringBuilder();

            content.append("local npc = Npc(").append(template.getId()).append(", ").append(template.getGfxId()).append(")");

            content.append(newLine);
            content.append(newLine);

            boolean needNewLine = false;

            if(template.getSex() != 0) {
                content.append("npc.gender = ").append(template.getSex());
                content.append(newLine);
                needNewLine = true;
            }
            if(template.getScaleX() != 100) {
                content.append("npc.scaleX = ").append(template.getScaleX());
                content.append(newLine);
                needNewLine = true;
            }
            if(template.getScaleY() != 100) {
                content.append("npc.scaleY = ").append(template.getScaleY());
                content.append(newLine);
                needNewLine = true;
            }
            if(template.getColor1() != -1 && template.getColor2() != -1 && template.getColor3() != -1) {
                content.append("npc.colors = {").append(template.getColor1()).append(", ").append(template.getColor2()).append(", ").append(template.getColor3()).append("}");
                content.append(newLine);
                needNewLine = true;
            }

            String accessories = StringUtils.join(ArrayUtils.toObject(template.getAccessories()), ", ");
            if(!accessories.equals("0, 0, 0, 0") && !accessories.equals("0, 0, 0, 0, 0")) {
                if(accessories.split(", ").length == 4)
                    accessories += ", 0";
                content.append("npc.accessories = {").append(accessories).append("}");
                content.append(newLine);
                needNewLine = true;
            }

            if(template.getCustomArtWork() != 0) {
                content.append("npc.customArtwork = ").append(template.getCustomArtWork());
                content.append(newLine);
                needNewLine = true;
            }
            if(template.getFlags() != 0) {
                content.append("npc.flags = ").append(template.getFlags());
                content.append(newLine);
                needNewLine = true;
            }

            if(needNewLine) {
                content.append(newLine);
            }

            //salesList
            if(!template.legacy.getAllItem().isEmpty()) {
                content.append("npc.sales = {").append(newLine);
                StringUtils.join(ArrayUtils.toObject(template.getAccessories()), ", ");
                content.append(template.legacy.getAllItem().stream().map(ObjectTemplate::getId)
                        .map((id) -> "    {item=" + id + "}").collect(Collectors.joining("," + newLine)));
                content.append(newLine).append("}");
                content.append(newLine).append(newLine);
            }

            // onTalk
            Integer question = template.legacy.getInitQuestions().get(-1);
            if(question == null || question == -1) {
                needNewLine = false;
                // No init question
            } else if(template.legacy.getInitQuestions().size() > 1) {
                StringBuilder data = new StringBuilder();
                for(Map.Entry<Integer, Integer> q : template.legacy.getInitQuestions().entrySet()) {
                    data.append(q.getKey()).append(",").append(q.getValue()).append(";");
                }
                content.append("--- MULTIPLE QUESTIONS (mapID,questionID;...) : ").append(data);
                needNewLine = true;
            } else {
                needNewLine = doQuestion(template, question, content);
            }

            // salesList


            // exchange
            //Voir barter NPC lua (voir git diabu)

            if(needNewLine)
                content.append(newLine);
            content.append("RegisterNPCDef(npc)");
            content.append(newLine);

            write(template, content.toString());
        }
    }

    private boolean doQuestion(NpcTemplate template, int question, StringBuilder content) {
        NpcQuestion q = World.world.getNPCQuestion(question);

        if(q != null) {
            String answers = q.getAnwsers().replace(";", ", ");

            content.append("---@param p Player").append(newLine).append("---@param answer number").append(newLine);
            content.append("function npc:onTalk(p, answer)");
            content.append(newLine);
            content.append("    if answer == 0 then p:ask(").append(question);

            if(!q.getAnwsers().isEmpty() || !"".equals(q.getArgs())) {
                content.append(", {").append(answers).append("}");
            }

            if(!"".equals(q.getArgs())) {
                content.append(", \"").append(q.getArgs()).append("\"");
            }
            content.append(")");
            content.append(newLine);
            doQuestionAnswers(template, q, content, new ArrayList<>());
            content.append("    end");
            content.append(newLine);
            content.append("end");
            content.append(newLine);
            return true;
        }
        return false;
    }

    private void doQuestionAnswers(NpcTemplate template, NpcQuestion question, StringBuilder content, List<Integer> knows) {
        if(knows.contains(question.getId())) return;
        knows.add(question.getId());

        // Collect all answers connected to question
        Map<Integer, String> map = Arrays.stream(question.getAnwsers().split(";"))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .map(World.world::getNpcAnswer)
                .filter(Objects::nonNull)
                .map(a -> new Pair<>(
                        a.getId(),
                        a.getActions().stream()
                                .filter((ac) -> ac != null && ac.getId() == 1)
                                .findAny()
                                .map(Action::getArgs)
                                .orElse(null)
                ))
                .filter(p -> p.getValue() != null)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        map.entrySet().forEach((entry) -> {
            if (entry.getValue().equals("DV")) {
                content.append("    elseif answer == ").append(entry.getKey()).append(" then ");
                content.append("p:endDialog()");
                content.append(newLine);
                return;
            }
            NpcQuestion question1 = World.world.getNPCQuestion(Integer.parseInt(entry.getValue()));
            if(question1 != null) {
                content.append("    elseif answer == ").append(entry.getKey()).append(" then ");
                content.append("p:ask(").append(entry.getValue());
                if(!question1.getAnwsers().isEmpty() || !"".equals(question1.getArgs())) {
                    content.append(", {").append(question1.getAnwsers().replace(";", ", ")).append("}");
                }

                if(!"".equals(question1.getArgs())) {
                    content.append(", \"").append(question1.getArgs()).append("\"");
                }
                content.append(")");
                content.append(newLine);
                doQuestionAnswers(template, question1, content, knows);
            }
        });
    }

    public void write(NpcTemplate template, String content) {
        try {
            String name = "";
            if(template.getName() != null) {
                name = "_" + template.getName().replace(' ', '_').replaceAll("\\p{M}", "");
                if(name.contains("?")) {
                    name = "";
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_DIRECTORY + template.getId() + name + ".lua"));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


