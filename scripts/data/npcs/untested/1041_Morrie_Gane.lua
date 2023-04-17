local npc = Npc(1041, 81)

npc.gender = 1
npc.colors = {2130958, 0, 14281765}
npc.accessories = {0, 9181, 8639, 8071, 9006}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5516)
    end
end

RegisterNPCDef(npc)
