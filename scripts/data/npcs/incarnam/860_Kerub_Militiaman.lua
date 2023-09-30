local npc = Npc(860, 9019)

npc.accessories = {8101, 0, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3675)
    end
end

RegisterNPCDef(npc)
