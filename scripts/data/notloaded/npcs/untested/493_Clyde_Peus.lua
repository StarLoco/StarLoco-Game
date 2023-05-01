local npc = Npc(493, 10)

npc.colors = {617037, 6697260, 10114366}
npc.accessories = {0, 0, 935, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2143)
    end
end

RegisterNPCDef(npc)
