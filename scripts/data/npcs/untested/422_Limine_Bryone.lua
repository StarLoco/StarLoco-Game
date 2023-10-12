local npc = Npc(422, 71)

npc.gender = 1
npc.colors = {0xb9f212, 0x658d44, 0x16a9e8}
npc.accessories = {0, 2097, 1695, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end
--TODO: check dialog offi en étant eni
RegisterNPCDef(npc)
