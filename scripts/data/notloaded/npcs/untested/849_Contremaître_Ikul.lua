local npc = Npc(849, 9037)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3596, {3182, 3183})
    elseif answer == 3182 then p:ask(3597, {3184, 3185, 3186, 3187, 3188})
    elseif answer == 3184 then p:ask(3598, {3189})
    elseif answer == 3189 then p:ask(3603)
    elseif answer == 3185 then p:ask(3599, {3190})
    elseif answer == 3190 then p:ask(3603)
    elseif answer == 3186 then p:ask(3600, {3191})
    elseif answer == 3191 then p:ask(3603)
    elseif answer == 3187 then p:ask(3601, {3192})
    elseif answer == 3192 then p:ask(3603)
    elseif answer == 3188 then p:ask(3602)
    elseif answer == 3183 then p:endDialog()
    end
end

RegisterNPCDef(npc)
