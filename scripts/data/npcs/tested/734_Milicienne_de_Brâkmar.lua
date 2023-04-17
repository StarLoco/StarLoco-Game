local npc = Npc(734, 81)

npc.gender = 1
npc.colors = {13969877, 15340586, 16513789}
npc.accessories = {0, 702, 0, 0, 7087}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3027, {2672})
    elseif answer == 2672 then
        if p:getItem(7939) then
            p:ask(3028, {})
        else
            p:addItem(7939)
            p:ask(3028, {})
        end
    end
end

RegisterNPCDef(npc)
