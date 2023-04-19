local npc = Npc(818, 111)

npc.gender = 1
npc.accessories = {0, 8457, 8458, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(8476) and p:getItem(8477)
    if p:mapID() == 9638 then
        if answer == 0 then
            if hasAllItems then
                p:ask(3415, {3032, 3026})
            else
                p:ask(3415, {3026})
            end
        elseif answer == 3026 then p:ask(3416, {3027})
        elseif answer == 3027 then p:ask(3417, {3028})
        elseif answer == 3028 then p:ask(3418, {3029})
        elseif answer == 3029 then p:ask(3419, {3030})
        elseif answer == 3030 then p:ask(3420, {3031})
        elseif answer == 3031 then p:ask(3421)
        elseif answer == 3032 then
            if hasAllItems then
                p:consumeItem(8476, 1)
                p:consumeItem(8477,1)
                p:teleport(10141, 448)
                p:endDialog()
            else
                p:endDialog()
            end
            end
    elseif p:mapID() == 10150 then
        if answer == 0 then
            p:ask(3422, {3033})
        elseif answer == 3033 then
            p:teleport(9638, 132)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
