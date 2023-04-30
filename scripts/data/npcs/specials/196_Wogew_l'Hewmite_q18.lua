local npc = Npc(196, 9047)
--TODO: Faire les dialogs quand quête fini & quand on a la quête avec seulement l'item "sang de wabbit gm" et pas l'analyse de sang ça met le dialog 988
--TODO: La quête est répétable
local questID = 18
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
	if answer == 0 then
	 if p:questFinished(questID) then  p:ask()
        elseif p:questOngoing(questID) then p:ask(986)
        else p:ask(985, {685})
		end
    elseif answer == 685 then
		p:startQuest(questID)
		p:ask(986)
    end
end

RegisterNPCDef(npc)
