local npc = Npc(399, 9056)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1667, {128})
    elseif answer == 128 then p:ask(164)
    end
end

RegisterNPCDef(npc)
