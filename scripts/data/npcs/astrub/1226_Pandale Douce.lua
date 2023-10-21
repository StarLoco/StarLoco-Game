local npc = Npc(1226, 40)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3341, {1303})
    elseif answer == 1303 then p:endDialog()
        p:teleport(10114, 282)
        p:modkamas(10)

    end
end

RegisterNPCDef(npc)
