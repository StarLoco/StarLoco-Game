local npc = Npc(814, 9037)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3388)
    end
end

RegisterNPCDef(npc)
