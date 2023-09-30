local npc = Npc(729, 9023)
--TODO: Lié à la quête 127, lorsqu'on lui parle en même temps que le dialog
--TODO: Quand on lui parle la première fois et qu'il lance la quête + le dialog 2981 ça lance un sorte de questionnaire
--TODO: Si on ferme le questionnaire sans réponse (donc je suppose pareil quand on lui reparle sans avoir rempli l'objectif de quête) ça donne le dialog 2989
npc.colors = {11756874, 16777215, 6249554}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2981, {2611, 2610})
    elseif answer == 2611 then p:ask(2983, {2612})
    elseif answer == 2610 then p:endDialog()
        --TODO: Finir les dialogs quand on lui parle la première fois, j'ai fermé donc pas pu finir
    end
end

RegisterNPCDef(npc)
