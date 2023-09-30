local npc = Npc(481, 30)
--TODO: Lié à la quête 15
npc.accessories = {0, 2097, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2111)
    end
end

RegisterNPCDef(npc)
