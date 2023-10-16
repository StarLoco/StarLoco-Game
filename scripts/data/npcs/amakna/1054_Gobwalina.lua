local npc = Npc(1054, 121)

npc.gender = 1
npc.accessories = {0, 2411, 2414, 35, 9025}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 9459 then
        if answer == 0 then
            p:ask(5774, {4981})
        elseif answer == 4981 then
            p:endDialog()
            p:teleport(9862, 395)
        end
    elseif p:mapID() == 9862 then
        if answer == 0 then
            p:ask(5775, {4982, 4983})
        elseif answer == 4982 then
            p:endDialog()
            p:teleport(9459, 360)
        elseif answer == 4983 then p:ask(5776, {4984})
        elseif answer == 4984 then p:ask(5777, {4985})
        elseif answer == 4985 then p:ask(5778)
        end
    end
end

RegisterNPCDef(npc)
