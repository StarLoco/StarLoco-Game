local npc = Npc(483, 91)

npc.gender = 1
npc.colors = {7775208, 657930, 5080297}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2083, {176})
    end
end

RegisterNPCDef(npc)
