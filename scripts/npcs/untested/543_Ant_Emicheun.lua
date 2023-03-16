local npc = Npc(543, 30)

npc.colors = {8336179, 10789889, 11718143}
npc.accessories = {0, 696, 759, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2314)
    end
end

RegisterNPCDef(npc)