local npc = Npc(863, 9031)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3739)
    end
end

RegisterNPCDef(npc)
