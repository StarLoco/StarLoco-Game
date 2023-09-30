local npc = Npc(614, 30)

npc.colors = {15761166, 14184973, 4801340}
npc.accessories = {0, 703, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2489)
    end
end

RegisterNPCDef(npc)
