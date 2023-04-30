local npc = Npc(495, 60)

npc.colors = {15285007, 13205057, 16777215}
npc.accessories = {0, 6813, 2547, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7143)
    end
end

RegisterNPCDef(npc)
