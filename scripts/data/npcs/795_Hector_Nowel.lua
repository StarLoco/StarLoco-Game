local npc = Npc(795, 30)

npc.colors = {16711680, 16777215, -1}
npc.accessories = {0, 8334, 8333, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 9468 then
        if answer == 0 then p:ask(3254, {2896})
        elseif answer == 2896 then p:ask(3257, {2897})
        elseif answer == 2897 then p:ask(3258, {2898, 2901})
        elseif answer == 2898 then p:ask(3259, {2900})
        elseif answer == 2900 then
            p:teleport(9979,213)
            p:endDialog()
        elseif answer == 2901 then p:ask(3260)
        end
    elseif p:mapID() == 9979 then
        if answer == 0 then p:ask(3255, {2902, 2904})
        elseif answer == 2902 then p:ask(3261, {2903})
        elseif answer == 2903 then
            p:teleport(9468,185)
            p:endDialog()
        elseif answer == 2904 then
            p:ask(3262)
        end
    end
end

RegisterNPCDef(npc)
