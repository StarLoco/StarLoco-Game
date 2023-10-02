local npc = Npc(613, 30)

npc.colors = {15761166, 14184973, 16777215}
npc.accessories = {0, 703, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2493)
    end
end

RegisterNPCDef(npc)
