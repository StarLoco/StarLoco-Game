local npc = Npc(664, 1264)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2719, {2327}, "10000")
    elseif answer == 2327 then p:ask(2741, {2368, 2369})
    elseif answer == 2368 then p:ask(2751)
    elseif answer == 2369 then p:ask(2752)
    end
end

RegisterNPCDef(npc)
