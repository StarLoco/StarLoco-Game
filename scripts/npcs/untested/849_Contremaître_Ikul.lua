local npc = Npc(849, 9037)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3596, {3182, 3183})
    elseif answer == 3182 then p:ask(3597, {3184, 3185, 3186, 3187, 3188})
    elseif answer == 3183 then p:endDialog()
    end
end

RegisterNPCDef(npc)