local npc = Npc(23, 9013)

npc.gender = 1
npc.colors = {8017470, 12288585, 16770534}

npc.sales = {
    {item= 770}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(87)
    end
end

RegisterNPCDef(npc)
