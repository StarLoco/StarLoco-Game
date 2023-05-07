local npc = Npc(672, 1260)
--TODO: Add échanges parchemins vs doplons
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2851, {6598, 6429, 6757})
        local npc = Npc(672, 1260)

        ---@param p Player
        ---@param answer number
        function npc:onTalk(p, answer)
            if answer == 0 then p:ask(2851, {6598, 6429, 6757})
            elseif answer == 6598 then p:ask(7108, {7355, 6798})
            elseif answer == 7355 then p:ask(7510, {7356, 7357, 7359, 7358})
            elseif answer == 7356 then p:ask(7511, {7361, 7360, 7365, 7363, 7364, 7362})
            elseif answer == 7361 then p:ask(7521, {7397, 7398})
            elseif answer == 7360 then p:ask(7520, {7395, 7396})
            elseif answer == 7365 then p:ask(7525, {7405, 7406})
            elseif answer == 7363 then p:ask(7523, {7401, 7402})
            elseif answer == 7364 then p:ask(7524, {7403, 7404})
            elseif answer == 7362 then p:ask(7522, {7399, 7400})
            elseif answer == 7397 or answer == 7395 or answer == 7405 or answer == 7401 or answer == 7403 or answer == 7399 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 7398 or answer == 7396 or answer == 7406 or answer == 7402 or answer == 7404 or answer == 7400 then
                p:ask(7519) ----TODO: Dialog si on a pas assez de doplons du temple, faire la réponse si on les a
            elseif answer == 6798 then p:endDialog()
            elseif answer == 7357 then p:ask(7512, {7375, 7372, 7371, 7373, 7374, 7376})
            elseif answer == 7375 then p:ask(7531, {7415, 7416})
            elseif answer == 7372 then p:ask(7528, {7409, 7410})
            elseif answer == 7371 then p:ask(7527, {7407, 7408})
            elseif answer == 7373 then p:ask(7529, {7411, 7412})
            elseif answer == 7374 then p:ask(7530, {7413, 7414})
            elseif answer == 7376 then p:ask(7532, {7417, 7418})
            elseif answer == 7415 or answer == 7409 or answer == 7407 or answer == 7411 or answer == 7413 or answer == 7417 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 7416 or answer == 7410 or answer == 7408 or answer == 7412 or answer == 7414 or answer == 7418 then
                p:ask(7519) ----TODO: Dialog si on a pas assez de doplons du temple, faire la réponse si on les a
            elseif answer == 7359 then p:ask(7514, {7388, 7391, 7387, 7392, 7390, 7389})
            elseif answer == 7388 then p:ask(7539, {7433, 7434})
            elseif answer == 7391 then p:ask(7542, {7439, 7440})
            elseif answer == 7387 then p:ask(7538, {7431, 7432})
            elseif answer == 7392 then p:ask(7543, {7441, 7442})
            elseif answer == 7390 then p:ask(7541, {7437, 7438})
            elseif answer == 7389 then p:ask(7540, {7435, 7436})
            elseif answer == 7433 or answer == 7439 or answer == 7431 or answer == 7441 or answer == 7437 or answer == 7435 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 7434 or answer == 7440 or answer == 7432 or answer == 7442 or answer == 7438 or answer == 7435 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 7358 then p:ask(7513, {7384, 7383, 7381, 7382, 7380, 7379})
            elseif answer == 7384 then p:ask(7537, {7429, 7430})
            elseif answer == 7383 then p:ask(7536, {7427, 7428})
            elseif answer == 7381 then p:ask(7534, {7423, 7424})
            elseif answer == 7382 then p:ask(7535, {7425, 7426})
            elseif answer == 7380 then p:ask(7533, {7421, 7422})
            elseif answer == 7379 then p:ask(7526, {7419, 7420})
            elseif answer == 7429 or answer == 7427 or answer == 7423 or answer == 7425 or answer == 7421 or answer == 7419 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 7430 or answer == 7428 or answer == 7424 or answer == 7426 or answer == 7422 or answer == 7420 then
                p:ask(7519) --TODO: Dialog si on a pas les doplons des 12 temples, faire la réponse si on les a
            elseif answer == 6429 then p:ask(7134, {6765})
            elseif answer == 6765 then
                --TODO: Lance combat vs Dopeul Panda
                p:endDialog()
            elseif answer == 6757 then p:ask(205)
            end
        end
    end
end
RegisterNPCDef(npc)
